package controller;

// Operator.
import operator.GTFSDataIndexer;

// Model.
import model.Route;
import model.ShapePoint;

// Altre classi.
import java.util.List;

/**
 * Controller per la gestione della mappa e dei layer.
 */
public class MapsController {

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private final GTFSDataIndexer dataIndexer;

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param dataIndexer operator contenente i dati GTFS indicizzati
     */
    public MapsController(GTFSDataIndexer dataIndexer) {
        this.dataIndexer = dataIndexer;
    }

    // RESTITUISCE GLI SHAPEPOINT DI UNA LINEA -------------------------------------------------------------------------
    /**
     * Restituisce la lista di punti geograici in base alla linea e alla direzione.
     *
     * @param routeId     ID della route
     * @param directionId ID della direzione (0 o 1 tipicamente)
     * @return            lista ordinata di {@link ShapePoint}
     */
    public List<ShapePoint> getBestShapeForRouteAndDirection(String routeId, int directionId) { return dataIndexer.getBestShapeForRouteAndDirection(routeId, directionId); }

    // RESTITUISCE LA LINEA TRAMITE ID ---------------------------------------------------------------------------------
    /**
     * Restituisce la linea corrispondente in base all'ID.
     *
     * @param routeId ID della linea
     * @return        oggetto {@link Route}
     */
    public Route getRouteById(String routeId) {
        return dataIndexer.getRouteById(routeId);
    }

    // RESTITUISCE LE POSIZIONI DEI VEICOLI PER UNA LINEA --------------------------------------------------------------
    /**
     * Restituisce le posizioni dei veicoli (real-time) in base alla linea.
     *
     * @param routeId ID della linea
     * @return        lista di {@link GTFSDataIndexer.VehiclePos}
     */
    public List<operator.GTFSDataIndexer.VehiclePos> getVehiclesForRoute(String routeId) {
        return dataIndexer.getVehiclesForRoute(routeId);
    }
}

