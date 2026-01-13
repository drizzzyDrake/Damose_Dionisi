package operator;

// Model.
import model.*;

// Altre classi.
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

// Protobuf GTFS-realtime.
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;

/**
 * Classe responsabile della costruzione dell'indice dei dati GTFS realtime in memoria.
 * <p>
 * Ricostruisce mappe temporanee e fa lo swap atomico delle reference volatile,
 * notifica listener registrati quando arrivano nuovi dati.
 * </p>
 */
public class GTFSRealtimeIndexer {

    // MAPPE TEMPORANEE (SWAP ATOMICO) ---------------------------------------------------------------------------------
    private volatile ConcurrentMap<String, GTFSDataIndexer.VehiclePos> tripVehiclePositions = new ConcurrentHashMap<>();
    private volatile ConcurrentMap<String, List<GTFSDataIndexer.PredictedArrival>> predictedArrivalsByStop = new ConcurrentHashMap<>();

    // LISTENER --------------------------------------------------------------------------------------------------------
    private final CopyOnWriteArrayList<Runnable> realtimeListeners = new CopyOnWriteArrayList<>();  // Lista di oggetti runnable.

    // RIFERIMENTI STATICI ---------------------------------------------------------------------------------------------
    private final GTFSStaticIndexer staticIndexer;

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param staticIndexer riferimento all'indicizzatore statico GTFS
     */
    public GTFSRealtimeIndexer(GTFSStaticIndexer staticIndexer) {
        this.staticIndexer = staticIndexer;
    }

    // CLEAR LISTENER --------------------------------------------------------------------------------------------------
    /**
     * Rimuove tutti i listener realtime registrati.
     */
    public void clearRealtimeListeners() {
        realtimeListeners.clear();
    }

    // CLEAR RISORSE ---------------------------------------------------------------------------------------------------
    /**
     * Svuota tutte le mappe realtime per liberare memoria.
     */
    public void clear() {
        tripVehiclePositions.clear();
        predictedArrivalsByStop.clear();
    }

    // APPLICA I FEED REALTIME -----------------------------------------------------------------------------------------
    /**
     * Applica gli aggiornamenti realtime dai feed GTFS.
     *
     * @param tripUpdatesFeed      feed con aggiornamenti dei trip
     * @param vehiclePositionsFeed feed con posizioni dei veicoli
     */
    public void applyRealtimeUpdates(FeedMessage tripUpdatesFeed, FeedMessage vehiclePositionsFeed) {
        try {
            // ----------------- trip id -> vehicle positions ----------------------------------------------------------
            ConcurrentMap<String, GTFSDataIndexer.VehiclePos> newVehiclePos = new ConcurrentHashMap<>();                    // Mappa temporanea per le nuove posizioni.

            if (vehiclePositionsFeed != null) {                                                                             // Se il feed delle posizioni non è nullo...
                long headerTs = (vehiclePositionsFeed.hasHeader() && vehiclePositionsFeed.getHeader().hasTimestamp())       // ...Recupera il timestamp dall'header del feed, se presente...
                        ? vehiclePositionsFeed.getHeader().getTimestamp() : (System.currentTimeMillis() / 1000L);           // ...Altrimenti utilizza l'orario corrente.

                for (FeedEntity e : vehiclePositionsFeed.getEntityList()) {                                                 // Per ogni entità del feed...
                    if (!e.hasVehicle()) continue;                                                                          // ...Ignora le entità senza informazioni di veicolo...
                    com.google.transit.realtime.GtfsRealtime.VehiclePosition vp = e.getVehicle();                           // ...Altrimenti (se presenti) salva le informazioni di veicolo...
                    if (!vp.hasTrip()) continue;                                                                            // ...Ignora le entità senza informazioni sulla corsa...
                    String tripId = vp.getTrip().getTripId();                                                               // ...Altrimenti (se presenti) salva le informazioni sulla corsa...
                    if (tripId == null || tripId.isEmpty()) continue;                                                       // ...Controlla che l'ID della corsa associata sia valido...
                    if (!vp.hasPosition()) continue;                                                                        // ...Ignora le entità senza informazioni di posizione...

                    double lat = vp.getPosition().getLatitude();                                                            // ...Recupera la latitudine del veicolo...
                    double lon = vp.getPosition().getLongitude();                                                           // ...Recupera la longitudine del veicolo...
                    long ts = headerTs;                                                                                     // ...Recupera la timestamp (data e ora) del veicolo...
                    String vehId = vp.hasVehicle() ? vp.getVehicle().getId() : null;                                        // ...Recupera l'id del veicolo...

                    Trip t = staticIndexer.getTripById(tripId);                                                             // ...Recupera la corsa associata tramite l'ID (staticIndexer)...
                    String routeId = (t != null) ? t.getRouteId() : null;                                                   // ...Recupera l'ID della linea tramite la corsa...
                    Integer routeType = null;                                                                               // ...Definsce la variabile del tipo di linea...
                    if (routeId != null) {                                                                                  // ...Se l'ID della linea è valido...
                        Route r = staticIndexer.getRouteById(routeId);                                                      // ...Recupera la linea associata tramite l'ID (staticIndexer)...
                        if (r != null) routeType = r.getRouteType();                                                        // ...Recupera il tipo di linea...
                    }
                    newVehiclePos.put(tripId, new GTFSDataIndexer.VehiclePos(vehId, lat, lon, ts, routeId, routeType));     // ...Aggiunge (ID corsa: oggetto VehiclePos) alla mappa temporanea.
                }
            }
            this.tripVehiclePositions = newVehiclePos;                                                                      // Salva la mappa in tripVehiclePositions.

            // ----------------- trip updates -> predicted arrivals per stop -------------------------------------------
            ConcurrentMap<String, List<GTFSDataIndexer.PredictedArrival>> newPredicted = new ConcurrentHashMap<>();         // Mappa temporanea per i nuovi arrivi previsti.

            if (tripUpdatesFeed != null) {                                                                                  // Se il feed degli arrivi previsti non è nullo...
                for (FeedEntity e : tripUpdatesFeed.getEntityList()) {                                                      // Per ogni entità del feed...
                    if (!e.hasTripUpdate()) continue;                                                                       // ...Ignora le entità senza informazioni sugli orari della corsa...
                    com.google.transit.realtime.GtfsRealtime.TripUpdate tu = e.getTripUpdate();                             // ...Altrimenti (se presenti) salva le informazioni sugli orari della corsa...
                    if (!tu.hasTrip()) continue;                                                                            // ...Ignora le entità senza informazioni sulla corsa associata...
                    String tripId = tu.getTrip().getTripId();                                                               // ...Altrimenti (se presenti) salva le informazioni sulla corsa associata...
                    if (tripId == null || tripId.isEmpty()) continue;                                                       // ...Controlla che l'ID della corsa associata sia valido...

                    for (com.google.transit.realtime.GtfsRealtime.TripUpdate.
                            StopTimeUpdate stu : tu.getStopTimeUpdateList()) {                                              // ...Per ogni stopTimeUpdate in tripUpdate (aggiornamenti degli arrivi alle fermate)...
                        if (!stu.hasStopId()) continue;                                                                     // ...Ignora gli stopTimeUpdate senza informazioni sulla fermata...
                        String stopId = stu.getStopId();                                                                    // ...Altrimenti (se presenti) salva le informazioni sull'ID della fermata...
                        long arrivalTime = -1L;                                                                             // -1 = valore sentinella (epoch seconds), indica nessun orario valido trovato.
                        if (stu.hasArrival() && stu.getArrival().hasTime()) {                                               // ...Recupera l'orario di arrivo in fermata...
                            arrivalTime = stu.getArrival().getTime();
                        }
                        if (arrivalTime <= 0) continue;                                                                     // ...Ignora se non è disponibile alcun orario (arrivalTime = -1)...

                        newPredicted.computeIfAbsent(stopId, k -> new ArrayList<>())
                                .add(new GTFSDataIndexer.PredictedArrival(tripId, arrivalTime));                            // ...Aggiunge (ID stop: lista di oggetti PredictedArrival) alla mappa temporanea.
                    }
                }
            }

            // Normalizza le liste nella mappa temporanea (ordina per tempo e rende immutabili), poi swap atomico.
            for (Map.Entry<String, List<GTFSDataIndexer.PredictedArrival>> en : newPredicted.entrySet()) {
                List<GTFSDataIndexer.PredictedArrival> list = en.getValue();
                list.sort(Comparator.comparingLong(pa -> pa.arrivalEpochSec));
                en.setValue(Collections.unmodifiableList(list));
            }
            this.predictedArrivalsByStop = new ConcurrentHashMap<>(newPredicted);                                           // Salva la mappa in predictedArrivalsByStop.

            // Notifica listener per UI.
            for (Runnable r : realtimeListeners) {      // Per ogni runnable (callback)...
                try {
                    r.run();                            // ...Avvia la callback (aggiorna la UI).
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // REGISTRA UN LISTENER ESEGUITO QUANDO ARRIVANO I DATI REALTIME ---------------------------------------------------
    /**
     * Aggiunge un listener eseguito ad ogni aggiornamento dei dati realtime.
     *
     * @param listener Runnable da eseguire all'arrivo dei dati
     */
    public void addRealtimeListener(Runnable listener) {
        if (listener != null) realtimeListeners.add(listener);
    }

    // GETTER PER POSIZIONI ATTUALI DEI VEICOLI SU UNA LINEA -----------------------------------------------------------
    /**
     * Restituisce la lista delle posizioni attuali dei veicoli in base alla linea.
     *
     * @param routeId ID della linea
     * @return        lista di {@link operator.GTFSDataIndexer.VehiclePos}
     */
    public List<GTFSDataIndexer.VehiclePos> getVehiclesForRoute(String routeId) {
        if (routeId == null) return Collections.emptyList();
        return tripVehiclePositions.values().stream()
                .filter(vp -> routeId.equals(vp.routeId))
                .collect(Collectors.toUnmodifiableList());
    }

    // RESTITUISCE GLI ARRIVI PREVISTI (PREDICTED) PER UNA FERMATA (OPZIONALMENTE FILTRATI PER ROUTE) ------------------
    /**
     * Restituisce gli arrivi previsti in base a fermata e linea (opzionale).
     *
     * @param stopId  ID della fermata
     * @param routeId ID della linea
     * @return        lista di {@link operator.GTFSDataIndexer.PredictedArrival}
     */
    public List<GTFSDataIndexer.PredictedArrival> getPredictedArrivals(String stopId, String routeId) {
        if (stopId == null) return Collections.emptyList();
        List<GTFSDataIndexer.PredictedArrival> list = predictedArrivalsByStop.getOrDefault(stopId, Collections.emptyList());
        if (list.isEmpty()) return Collections.emptyList();
        if (routeId == null) return list;

        // Filtra per route usando il trip -> tripId -> trip -> routeId
        List<GTFSDataIndexer.PredictedArrival> filtered = new ArrayList<>();
        for (GTFSDataIndexer.PredictedArrival pa : list) {
            Trip t = staticIndexer.getTripById(pa.tripId);
            if (t != null && routeId.equals(t.getRouteId())) filtered.add(pa);
        }
        return Collections.unmodifiableList(filtered);
    }
}



