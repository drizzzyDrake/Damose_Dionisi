package operator;

// Model.
import model.*;

// Altre classi.
import java.util.*;

/**
 * Classe responsabile della costruzione dell'indice dei dati GTFS statici in memoria.
 * <p>
 * Si basa sui repository già caricati da {@link GTFSStaticLoader},
 * fornisce metodi di accesso ottimizzati per ottenere le relazioni tra i dati GTFS.
 * </p>
 */
public class GTFSStaticIndexer {

    // MAPPE PER ACCESSO A GTFS STATICI --------------------------------------------------------------------------------
    private final Map<String, List<ShapePoint>> shapePointsByShapeId = new HashMap<>();
    private final Map<String, Stop> stopsById = new HashMap<>();
    private final Map<String, Route> routesById = new HashMap<>();
    private final Map<String, Trip> tripsById = new HashMap<>();
    private final Map<String, List<Trip>> tripsByRoute = new HashMap<>();
    private final Map<String, Map<Integer, List<Trip>>> tripsByRouteAndDirection = new HashMap<>();
    private final Map<String, List<StopTime>> stopTimesByTrip = new HashMap<>();
    private final Map<String, List<StopTime>> stopTimesByStop = new HashMap<>();

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     * <p>
     * Costruisce l'indicizzatore statico caricando tutti i dati dai repository forniti dal loader.
     * Costruisce mappe ottimizzate per accesso rapido a shape points, stops, routes, trips e stop times.
     * </p>
     * @param loader istanza di {@link GTFSStaticLoader} contenente i repository già caricati
     */
    public GTFSStaticIndexer(GTFSStaticLoader loader) {

        // shape points
        loader.getShapePointRepository().getAllShapePoints().forEach(sp ->
                shapePointsByShapeId.computeIfAbsent(sp.getShapeId(), k -> new ArrayList<>()).add(sp)
        );
        shapePointsByShapeId.values().forEach(list ->
                list.sort(Comparator.comparingInt(ShapePoint::getShapePtSequence))
        );

        // stops
        loader.getStopRepository().getAllStops().forEach(s -> stopsById.put(s.getStopId(), s));

        // routes
        loader.getRouteRepository().getAllRoutes().forEach(r -> routesById.put(r.getRouteId(), r));

        // trips
        loader.getTripRepository().getAllTrips().forEach(t -> {
            tripsById.put(t.getTripId(), t);
            tripsByRoute.computeIfAbsent(t.getRouteId(), k -> new ArrayList<>()).add(t);
            tripsByRouteAndDirection
                    .computeIfAbsent(t.getRouteId(), k -> new HashMap<>())
                    .computeIfAbsent(t.getDirectionId(), k -> new ArrayList<>())
                    .add(t);
        });

        // stop times
        loader.getStopTimeRepository().getAllStopTimes().forEach(st -> {
            stopTimesByTrip.computeIfAbsent(st.getTripId(), k -> new ArrayList<>()).add(st);
            stopTimesByStop.computeIfAbsent(st.getStopId(), k -> new ArrayList<>()).add(st);
        });

        // ordina stopTimes per trip
        stopTimesByTrip.values().forEach(list ->
                list.sort(Comparator.comparingInt(StopTime::getStopSequence))
        );
    }

    // CLEAR RISORSE ---------------------------------------------------------------------------------------------------
    /**
     * Svuota tutte le mappe statiche per liberare memoria.
     */
    public void clear() {
        shapePointsByShapeId.clear();
        stopsById.clear();
        routesById.clear();
        tripsById.clear();
        tripsByRoute.clear();
        tripsByRouteAndDirection.clear();
        stopTimesByTrip.clear();
        stopTimesByStop.clear();
    }

    // GETTER PER LISTE COMPLETE ---------------------------------------------------------------------------------------
    /**
     * Restituisce tutte le fermate disponibili.
     *
     * @return collezione di {@link Stop}
     */
    public Collection<Stop> getAllStops() {
        return Collections.unmodifiableCollection(stopsById.values());
    }

    // GETTER PER LISTE DI ID ------------------------------------------------------------------------------------------
    /**
     * Restituisce tutti gli ID delle linee disponibili.
     *
     * @return insieme di {@link Route}
     */
    public Set<String> getAllRouteIds() {
        return Collections.unmodifiableSet(routesById.keySet());
    }

    // GETTER PER ELEMENTO TRAMITE ID ----------------------------------------------------------------------------------
    /**
     * Restituisce tutti gli shape in base all'ID.
     *
     * @param shapeId ID dello shape
     * @return        lista di {@link ShapePoint}
     */
    public List<ShapePoint> getShapePointsByShapeId(String shapeId) {
        return shapeId == null ? Collections.emptyList()
                : Collections.unmodifiableList(shapePointsByShapeId.getOrDefault(shapeId, Collections.emptyList()));
    }

    /**
     * Restituisce la linea i base all'ID.
     *
     * @param routeId ID della linea
     * @return        oggetto {@link Route}+
     */
    public Route getRouteById(String routeId) { return routesById.get(routeId); }

    /**
     * Restituisce la fermata in base all'ID.
     *
     * @param stopId ID della fermata
     * @return       oggetto {@link Stop}
     */
    public Stop getStopById(String stopId) { return stopsById.get(stopId); }

    /**
     * Restituisce la corsa in base all'ID.
     *
     * @param tripId ID della corsa
     * @return       oggetto {@link Trip}
     */
    public Trip getTripById(String tripId) { return tripsById.get(tripId); }

    // GETTER PER ELEMENTO TRAMITE ALTRO ELEMENTO ----------------------------------------------------------------------
    /**
     * Restituisce la shape più completa (più punti) per una route e direzione.
     *
     * @param routeId     ID della route
     * @param directionId ID della direzione (0 o 1 tipicamente)
     * @return            lista ordinata di {@link ShapePoint}
     */
    public List<ShapePoint> getBestShapeForRouteAndDirection(String routeId, int directionId) {
        List<Trip> tripsForRouteDir = getTripsByRouteAndDirection(routeId, directionId);
        if (tripsForRouteDir == null || tripsForRouteDir.isEmpty()) {
            return Collections.emptyList();
        }

        String bestShapeId = null;
        int maxPoints = -1;

        for (Trip t : tripsForRouteDir) {
            String sid = t.getShapeId();
            if (sid == null || sid.isBlank()) continue;

            List<ShapePoint> pts = getShapePointsByShapeId(sid);
            if (pts.size() > maxPoints) {
                maxPoints = pts.size();
                bestShapeId = sid;
            }
        }

        if (bestShapeId == null) {
            return Collections.emptyList();
        }

        return getShapePointsByShapeId(bestShapeId);
    }

    /**
     * Restituisce tutte le corse in base alla linea.
     *
     * @param routeId ID della linea
     * @return        lista di {@link Trip}
     */
    public List<Trip> getTripsByRoute(String routeId) {
        return routeId == null ? Collections.emptyList()
                : Collections.unmodifiableList(tripsByRoute.getOrDefault(routeId, Collections.emptyList()));
    }

    /**
     * Restituisce tutti gli stop times in base alla corsa.
     *
     * @param tripId ID della corsa
     * @return       lista di {@link StopTime}
     */
    public List<StopTime> getStopTimesByTrip(String tripId) {
        return tripId == null ? Collections.emptyList()
                : Collections.unmodifiableList(stopTimesByTrip.getOrDefault(tripId, Collections.emptyList()));
    }

    /**
     * Restituisce tutti gli stop times in base alla fermata.
     *
     * @param stopId ID della fermata
     * @return       lista di {@link StopTime}
     */
    public List<StopTime> getStopTimesByStop(String stopId) {
        return stopId == null ? Collections.emptyList()
                : Collections.unmodifiableList(stopTimesByStop.getOrDefault(stopId, Collections.emptyList()));
    }

    /**
     * Restituisce tutte le corse in base a linea e direzione.
     *
     * @param routeId     ID della linea
     * @param directionId ID della direzione
     * @return            lista di {@link Trip}
     */
    public List<Trip> getTripsByRouteAndDirection(String routeId, int directionId) {
        if (routeId == null) return Collections.emptyList();
        Map<Integer, List<Trip>> m = tripsByRouteAndDirection.get(routeId);
        if (m == null) return Collections.emptyList();
        return Collections.unmodifiableList(m.getOrDefault(directionId, Collections.emptyList()));
    }
}


