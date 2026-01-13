package operator;

// Repository.
import model.repository.*;

// Altre classi.
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

/**
 * Classe responsabile del caricamento dei dati GTFS statici.
 * <p>
 * Carica tutti i file GTFS statici una sola volta all’avvio dell’applicazione,
 * inizializza e conserva le repository già pronte permettendo ai controller accessi diretti ai dati,
 * centralizza il caricamento e mantiene il progetto ordinato e modulare.
 * Senza {@link GTFSStaticLoader}, ogni chiamata alle Repository implicherebbe
 * una lettura e parsatura dei file da zero.
 * </p>
 */
public class GTFSStaticLoader {

    // REPOSITORY PER FILE GTFS ----------------------------------------------------------------------------------------
    private final AgencyRepository agencyRepository = new AgencyRepository();
    private final CalendarDateRepository calendarDateRepository = new CalendarDateRepository();
    private final RouteRepository routeRepository = new RouteRepository();
    private final ShapePointRepository shapePointRepository = new ShapePointRepository();
    private final TripRepository tripRepository = new TripRepository();
    private final StopRepository stopRepository = new StopRepository();
    private final StopTimeRepository stopTimeRepository = new StopTimeRepository();

    // CARICA TUTTI I FILE GTFS DALLA CACHE (LOCALE) -------------------------------------------------------------------
    /**
     * Carica tutti i file GTFS statici da una directory locale.
     *
     * @param dir          percorso della directory contenente i file GTFS
     * @throws IOException in caso di file non trovato o errore di lettura
     */
    public void loadAllFromDirectory(java.nio.file.Path dir) throws IOException {
        loadAgency(dir.resolve("agency.txt").toString());
        loadCalendarDates(dir.resolve("calendar_dates.txt").toString());
        loadRoutes(dir.resolve("routes.txt").toString());
        loadShapePoints(dir.resolve("shapes.txt").toString());
        loadTrips(dir.resolve("trips.txt").toString());
        loadStops(dir.resolve("stops.txt").toString());
        loadStopTimes(dir.resolve("stop_times.txt").toString());
    }

    // CARICAMENTO CONDIZIONATO DEI FILE GTFS --------------------------------------------------------------------------
    private void loadAgency(String resourcePath) throws IOException {
        try (InputStream is = resourcePath.startsWith("/") ?
                getClass().getResourceAsStream(resourcePath) :
                new FileInputStream(resourcePath)) {
            if (is == null) throw new IOException("Resource non trovata: " + resourcePath);
            agencyRepository.loadAgenciesFromStream(is);
        }
    }

    private void loadCalendarDates(String resourcePath) throws IOException {
        try (InputStream is = resourcePath.startsWith("/") ?
                getClass().getResourceAsStream(resourcePath) :
                new FileInputStream(resourcePath)) {
            if (is == null) throw new IOException("Resource non trovata: " + resourcePath);
            calendarDateRepository.loadCalendarDatesFromStream(is);
        }
    }

    private void loadRoutes(String resourcePath) throws IOException {
        try (InputStream is = resourcePath.startsWith("/") ?
                getClass().getResourceAsStream(resourcePath) :
                new FileInputStream(resourcePath)) {
            if (is == null) throw new IOException("Resource non trovata: " + resourcePath);
            routeRepository.loadRoutesFromStream(is);
        }
    }

    private void loadShapePoints(String resourcePath) throws IOException {
        try (InputStream is = resourcePath.startsWith("/") ?
                getClass().getResourceAsStream(resourcePath) :
                new FileInputStream(resourcePath)) {
            if (is == null) throw new IOException("Resource non trovata: " + resourcePath);
            shapePointRepository.loadShapePointsFromStream(is);
        }
    }

    private void loadTrips(String resourcePath) throws IOException {
        try (InputStream is = resourcePath.startsWith("/") ?
                getClass().getResourceAsStream(resourcePath) :
                new FileInputStream(resourcePath)) {
            if (is == null) throw new IOException("Resource non trovata: " + resourcePath);
            tripRepository.loadTripsFromStream(is);
        }
    }

    private void loadStops(String resourcePath) throws IOException {
        try (InputStream is = resourcePath.startsWith("/") ?
                getClass().getResourceAsStream(resourcePath) :
                new FileInputStream(resourcePath)) {
            if (is == null) throw new IOException("Resource non trovata: " + resourcePath);
            stopRepository.loadStopsFromStream(is);
        }
    }

    private void loadStopTimes(String resourcePath) throws IOException {
        try (InputStream is = resourcePath.startsWith("/") ?
                getClass().getResourceAsStream(resourcePath) :
                new FileInputStream(resourcePath)) {
            if (is == null) throw new IOException("Resource non trovata: " + resourcePath);
            stopTimeRepository.loadStopTimesFromStream(is);
        }
    }

    // GETTER PER ACCEDERE ALLE REPOSITORY -----------------------------------------------------------------------------
    /**
     * Restituisce la repository delle agenzie GTFS
     *
     * @return {@link AgencyRepository}
     */
    public AgencyRepository getAgencyRepository() { return agencyRepository; }

    /**
     * Restituisce la repository delle date GTFS
     *
     * @return {@link CalendarDateRepository}
     */
    public CalendarDateRepository getCalendarDateRepository() { return calendarDateRepository; }

    /**
     * Restituisce la repository delle linee GTFS
     *
     * @return {@link RouteRepository}
     */
    public RouteRepository getRouteRepository() { return routeRepository; }

    /**
     * Restituisce la repository delle shape GTFS
     *
     * @return {@link ShapePointRepository}
     */
    public ShapePointRepository getShapePointRepository() { return shapePointRepository; }

    /**
     * Restituisce la repository delle corse GTFS
     *
     * @return {@link TripRepository}
     */
    public TripRepository getTripRepository() { return tripRepository; }

    /**
     * Restituisce la repository delle fermate GTFS
     *
     * @return {@link StopRepository}
     */
    public StopRepository getStopRepository() { return stopRepository; }

    /**
     * Restituisce la repository degli stop time GTFS
     *
     * @return {@link StopTimeRepository}
     */
    public StopTimeRepository getStopTimeRepository() { return stopTimeRepository; }
}


