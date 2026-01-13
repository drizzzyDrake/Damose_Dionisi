package operator;

// Model.
import model.*;

// Altre classi.
import java.util.*;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

/**
 * Facade che espone API unificate per accedere ai dati GTFS:
 * <p>
 * <ul>
 *     <li>dati statici tramite {@link GTFSStaticIndexer}</li>
 *     <li>dati realtime tramite {@link GTFSRealtimeIndexer}</li>
 * </ul>
 */
public class GTFSDataIndexer {

    // INDEXER DELEGATI ------------------------------------------------------------------------------------------------
    private final GTFSStaticIndexer staticIndexer;
    private final GTFSRealtimeIndexer realtimeIndexer;

    // NESTED TYPES ---------------------------------------------------------------------------------------------------
    /**
     * Classe che rappresenta la posizione di un veicolo in tempo reale.
     */
    public static class VehiclePos {

        /** Identificatore del veicolo. */
        public final String vehicleId;

        /** Latitudine del veicolo. */
        public final double lat;

        /** Longitudine del veicolo. */
        public final double lon;

        /** Timestamp della posizione (epoch second). */
        public final long ts;

        /** Identificatore della linea associata. */
        public final String routeId;

        /** Tipo di mezzo associato alla linea. */
        public final Integer routeType;

        /**
         * Costruttore.
         *
         * @param vehicleId identificatore del veicolo
         * @param lat       latitudine
         * @param lon       longitudine
         * @param ts        timestamp (epoch second)
         * @param routeId   identificatore della linea associata
         * @param routeType tipo di mezzo
         */
        public VehiclePos(String vehicleId,
                          double lat,
                          double lon,
                          long ts,
                          String routeId,
                          Integer routeType) {
            this.vehicleId = vehicleId;
            this.lat = lat;
            this.lon = lon;
            this.ts = ts;
            this.routeId = routeId;
            this.routeType = routeType;
        }
    }

    /**
     * Classe che rappresenta un arrivo previsto per una corsa.
     */
    public static class PredictedArrival {

        /** Identificatore della corsa. */
        public final String tripId;

        /** Orario di arrivo previsto (epoch second). */
        public final long arrivalEpochSec;

        /**
         * Costruttore.
         *
         * @param tripId          identificatore della corsa
         * @param arrivalEpochSec orario di arrivo previsto (epoch second)
         */
        public PredictedArrival(String tripId, long arrivalEpochSec) {
            this.tripId = tripId;
            this.arrivalEpochSec = arrivalEpochSec;
        }
    }

    // COSTRUTTORE ---------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param loader loader per i dati statici GTFS
     */
    public GTFSDataIndexer(GTFSStaticLoader loader) {
        this.staticIndexer = new GTFSStaticIndexer(loader);
        this.realtimeIndexer = new GTFSRealtimeIndexer(this.staticIndexer);
    }

    // CLEAR LISTENERS DELEGATI ----------------------------------------------------------------------------------------
    /**
     * Svuota i listener realtime.
     */
    public void removeRealtimeListeners() {
        realtimeIndexer.clearRealtimeListeners();
    }

    // CLEAR RISORSE DELEGATI ------------------------------------------------------------------------------------------
    /**
     * Svuota tutte le mappe statiche e realtime.
     */
    public void clear() {
        staticIndexer.clear();       // Svuota mappe statiche.
        realtimeIndexer.clear();     // Svuota mappe realtime.
    }


    // METODI REALTIME DELEGATI --------------------------------------------------------------------------------------
    /**
     * Applica aggiornamenti realtime ai dati.
     *
     * @param tripUpdates      Feed dei trip updates
     * @param vehiclePositions Feed delle posizioni dei veicoli
     */
    public void applyRealtimeUpdates(FeedMessage tripUpdates, FeedMessage vehiclePositions) {
        realtimeIndexer.applyRealtimeUpdates(tripUpdates, vehiclePositions);
    }

    /**
     * Aggiunge un listener che viene notificato ad ogni aggiornamento realtime.
     *
     * @param listener Runnable da eseguire all'aggiornamento
     */
    public void addRealtimeListener(Runnable listener) {
        realtimeIndexer.addRealtimeListener(listener);
    }

    /**
     * Restituisce le posizioni dei veicoli in base alla route.
     *
     * @param routeId ID della route
     * @return        lista di posizioni dei veicoli
     */
    public List<VehiclePos> getVehiclesForRoute(String routeId) {
        return realtimeIndexer.getVehiclesForRoute(routeId);
    }

    /**
     * Restituisce gli arrivi previsti in base a fermata e linea.
     *
     * @param stopId  ID della fermata
     * @param routeId ID della route (può essere null)
     * @return        lista di arrivi previsti
     */
    public List<PredictedArrival> getPredictedArrivals(String stopId, String routeId) {
        List<PredictedArrival> preds = realtimeIndexer.getPredictedArrivals(stopId, routeId);
        if (preds == null) return Collections.emptyList();
        return preds;
    }

    // METODI STATICI DELEGATI --------------------------------------------------------------------------------------
    /**
     * Restituisce tutte le fermate.
     *
     * @return collezione di {@link Stop}
     */
    public Collection<Stop> getAllStops() { return staticIndexer.getAllStops(); }

    /**
     * Restituisce tutti gli ID in base alla linea.
     *
     * @return insieme di ID di routeid
     */
    public Set<String> getAllRouteIds() { return staticIndexer.getAllRouteIds(); }

    /**
     * Restituisce la linea in base all'ID.
     *
     * @param routeId ID della linea
     * @return        oggetto {@link Route}
     */
    public Route getRouteById(String routeId) { return staticIndexer.getRouteById(routeId); }

    /**
     * Restituisce la fermata in base all'ID.
     *
     * @param stopId ID della fermata
     * @return       oggetto {@link Stop}
     */
    public Stop getStopById(String stopId) { return staticIndexer.getStopById(stopId); }

    /**
     * Restituisce la corsa in base all'ID.
     *
     * @param tripId ID della corsa
     * @return       oggetto {@link Trip}
     */
    public Trip getTripById(String tripId) { return staticIndexer.getTripById(tripId); }

    /**
     * Restituisce la shape più completa (più punti) per una route e direzione.
     *
     * @param routeId     ID della route
     * @param directionId ID della direzione (0 o 1 tipicamente)
     * @return            lista ordinata di {@link ShapePoint}
     */
    public List<ShapePoint> getBestShapeForRouteAndDirection(String routeId, int directionId) { return staticIndexer.getBestShapeForRouteAndDirection(routeId, directionId); }

    /**
     * Restituisce tutte le corse in base alla linea.
     *
     * @param routeId ID della linea
     * @return        lista di {@link Trip}
     */
    public List<Trip> getTripsByRoute(String routeId) { return staticIndexer.getTripsByRoute(routeId); }

    /**
     * Restituisce gli stop times in base alla corsa.
     *
     * @param tripId ID della corsa
     * @return       lista di {@link StopTime}
     */
    public List<StopTime> getStopTimesByTrip(String tripId) { return staticIndexer.getStopTimesByTrip(tripId); }

    /**
     * Restituisce gli stop times in base alla fermata.
     *
     * @param stopId ID della fermata
     * @return       lista di {@link StopTime}
     */
    public List<StopTime> getStopTimesByStop(String stopId) { return staticIndexer.getStopTimesByStop(stopId); }

    /**
     * Restituisce le corse di una linea in base alla direzione.
     *
     * @param routeId     ID della linea
     * @param directionId ID della direzione
     * @return            lista {@link Trip}
     */
    public List<Trip> getTripsByRouteAndDirection(String routeId, int directionId) { return staticIndexer.getTripsByRouteAndDirection(routeId, directionId); }
}











