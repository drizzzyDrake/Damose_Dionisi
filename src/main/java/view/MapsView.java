package view;

// Model.
import model.Route;
import model.ShapePoint;
import model.Stop;
import model.Trip;

// Operator.
import operator.GTFSDataIndexer;
import operator.TilesManager;

// Controller.
import controller.MapsController;

// Layout e nodi.
import view.layouts.MapsLayout;
import view.nodes.MapPanel;

// Classi javaFX necessarie per mostrare la GUI.
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

// Altre classi.
import java.util.ArrayList;
import java.util.List;

/**
 * Classe view per la mappa.
 * <p>
 * Compone la GUI della mappa con SwingNode.
 */
public class MapsView {

    // ROOT ------------------------------------------------------------------------------------------------------------
    private StackPane mapsRoot;                                 // Layout root della mappa.

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private TilesManager tilesManager;                          // Gestore tiles

    // CONTROLLER ------------------------------------------------------------------------------------------------------
    private final MapsController mapsController;                // Controller della mappa.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final MapsLayout mapsLayout;                        // Layout della mappa.
    private MapPanel map;                                       // Mappa Swing.
    private Pane overlay;                                       // Overlay trasparente per modalità non fullscreen.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private Trip currentTrip;                                   // Trip attualmente visualizzato

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param mapsController controller della mappa.
     */
    public MapsView(MapsController mapsController) {
        this.mapsController = mapsController;
        this.mapsLayout = new MapsLayout();
    }

    // CREAZIONE DELLA UI ----------------------------------------------------------------------------------------------
    /**
     * Crea l'interfaccia della mappa, assembla tutti i layout e le view.
     */
    private void createView() {

        tilesManager = new TilesManager();

        double latMin = 41.6, latMax = 42.2;
        double lonMin = 12.1, lonMax = 12.8;
        int zoom = 11;

        SwingNode swingNode = new SwingNode();

        javax.swing.SwingUtilities.invokeLater(() -> {
            map = new MapPanel(tilesManager, latMin, latMax, lonMin, lonMax, zoom);
            swingNode.setContent(map);
        });

        overlay = new Pane();
        overlay.setStyle("-fx-background-color: TRANSPARENT;");

        mapsRoot = mapsLayout.createMapsRoot(swingNode);

        mapsRoot.getChildren().add(overlay);
        StackPane.setAlignment(overlay, javafx.geometry.Pos.TOP_LEFT);
        overlay.prefWidthProperty().bind(mapsRoot.widthProperty());
        overlay.prefHeightProperty().bind(mapsRoot.heightProperty());
    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Restituisce la mappa.
     * Crea la view se non è stata ancora inizializzata.
     *
     * @return BorderPane contenente la view.
     */
    public StackPane getView() {
        if (mapsRoot == null) createView();
        return mapsRoot;
    }

    public Pane getOverlay() {
        return overlay;
    }

    // MOSTRA UNA FERMATA ----------------------------------------------------------------------------------------------
    /**
     * Mostra una fermata sulla mappa con animazione.
     */
    public void showStopOnMap(Stop stop) {
        if (map == null) getView();

        if (currentTrip != null) {
            Route route = mapsController.getRouteById(currentTrip.getRouteId());
            map.drawStops(List.of(new double[]{stop.getStopLat(), stop.getStopLon()}), route);
        } else {
            map.drawStops(List.of(new double[]{stop.getStopLat(), stop.getStopLon()}), null);
        }

        double fromLat = map.getCenterLat();
        double fromLon = map.getCenterLon();
        int fromZoom = (int) map.getZoomLevel();

        double toLat = stop.getStopLat();
        double toLon = stop.getStopLon();

        double distKm = distanceKm(fromLat, fromLon, toLat, toLon);

        if (distKm < 2.0) {
            int targetZoom = Math.min(fromZoom, 17);
            map.animateZoomAndPan(fromLat, fromLon, toLat, toLon,
                    fromZoom, targetZoom, 500, null);
        } else {
            int zoomOut = Math.max(fromZoom - 3, 11);
            int zoomIn = 17;
            map.animateZoomAndPan(fromLat, fromLon, fromLat, fromLon,
                    fromZoom, zoomOut, 300,
                    () -> map.animateZoomAndPan(fromLat, fromLon, toLat, toLon,
                            zoomOut, zoomIn, 700, null));
        }
    }

    // MOSTRA UNA LINEA ------------------------------------------------------------------------------------------------
    /**
     * Mostra una linea sulla mappa con veicoli e animazione.
     */
    public void showLineOnMap(Trip trip) {
        if (map == null) getView();

        this.currentTrip = trip;

        // recupero shape
        List<model.ShapePoint> points =
                mapsController.getBestShapeForRouteAndDirection(trip.getRouteId(), trip.getDirectionId());

        if (points == null || points.isEmpty()) return;

        List<double[]> shapeCoords = new ArrayList<>();
        for (ShapePoint p : points) {
            shapeCoords.add(new double[]{p.getShapePtLat(), p.getShapePtLon()});
        }

        Route route = mapsController.getRouteById(trip.getRouteId());
        map.drawShapes(shapeCoords, route);

        List<GTFSDataIndexer.VehiclePos> vehicles =
                mapsController.getVehiclesForRoute(trip.getRouteId());

        if (vehicles != null && !vehicles.isEmpty()) {
            List<double[]> vehCoords = new ArrayList<>();
            for (GTFSDataIndexer.VehiclePos v : vehicles) {
                vehCoords.add(new double[]{v.lat, v.lon});
            }
            map.drawVehicles(vehCoords);
        } else {
            map.clearVehicles();
        }

        double[] first = shapeCoords.get(0);

        double fromLat = map.getCenterLat();
        double fromLon = map.getCenterLon();
        int fromZoom = (int) map.getZoomLevel();

        double toLat = first[0];
        double toLon = first[1];

        double distKm = distanceKm(fromLat, fromLon, toLat, toLon);

        if (distKm < 3.0) {
            int targetZoom = Math.min(fromZoom, 15);
            map.animateZoomAndPan(fromLat, fromLon, toLat, toLon,
                    fromZoom, targetZoom, 500, null);
        } else {
            int zoomOut = Math.max(fromZoom - 3, 11);
            int zoomIn = 15;
            map.animateZoomAndPan(fromLat, fromLon, fromLat, fromLon,
                    fromZoom, zoomOut, 300,
                    () -> map.animateZoomAndPan(fromLat, fromLon, toLat, toLon,
                            zoomOut, zoomIn, 700, null));
        }
    }

    // MOSTRA I VEICOLI ------------------------------------------------------------------------------------------------
    /**
     * Mostra i veicoli sulla mappa.
     */
    public void refreshVehiclesLayer() {
        if (map == null || currentTrip == null) return;

        String routeId = currentTrip.getRouteId();

        List<GTFSDataIndexer.VehiclePos> vehicles =
                mapsController.getVehiclesForRoute(routeId);

        javax.swing.SwingUtilities.invokeLater(() -> {
            if (vehicles != null && !vehicles.isEmpty()) {
                List<double[]> vehCoords = new ArrayList<>();
                for (GTFSDataIndexer.VehiclePos v : vehicles) {
                    vehCoords.add(new double[]{v.lat, v.lon});
                }
                map.drawVehicles(vehCoords);
            } else {
                map.clearVehicles();
            }
        });
    }

    // CALCOLA LA DISTANZA TRA DUE PUNTI SULLA MAPPA -------------------------------------------------------------------
    /**
     * Calcola la distanza haversine tra due punti sulla mappa.
     */
    private double distanceKm(double lat1, double lon1,
                              double lat2, double lon2) {

        final double R = 6371.0;

        double aLat = Math.toRadians(lat1);
        double bLat = Math.toRadians(lat2);
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double sinDlat = Math.sin(dLat / 2);
        double sinDlon = Math.sin(dLon / 2);

        double a = sinDlat * sinDlat
                + Math.cos(aLat) * Math.cos(bLat) * sinDlon * sinDlon;

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
