package controller;

// Model.
import model.Route;
import model.Stop;
import model.Trip;

// Operator.
import operator.GTFSDataIndexer;

// Altre classi.
import java.util.*;

/**
 * Controller per la ricerca e gestione delle linee.
 */
public class FindLineController {

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private final GTFSDataIndexer dataIndexer;        // GTFS statici indicizzati.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param dataIndexer operator contenente i dati GTFS indicizzati
     */
    public FindLineController(GTFSDataIndexer dataIndexer) {
        this.dataIndexer = dataIndexer;
    }

    // RICERCA LE LINEE IN BASE ALLA QUERY DELL'UTENTE -----------------------------------------------------------------
    /**
     * Cerca le linee in base a una query testuale dell'utente.
     * <p>
     * La ricerca controlla se la query corrisponde al nome breve della linea.
     * Per ogni linea corrispondente, vengono generate stringhe di suggerimento
     * contenenti il nome breve, la destinazione e l'ID della route e della direzione.
     * </p>
     *
     * @param query stringa di ricerca dell'utente
     * @return      lista di suggerimenti contenenti ID, nome e destinazione della corsa
     */
    public List<String> searchLines(String query) {

        List<String> suggestions = new ArrayList<>();                                       // Lista di linee che vengono visualizzate come suggerimento.

        for (String routeId : dataIndexer.getAllRouteIds()) {                               // Per ogni route_id...
            Route route = dataIndexer.getRouteById(routeId);                                // ...Ottiene la route corrente...
            if (route == null) continue;

            String shortName = route.getRouteShortName();                                   // ...Ottiene il nome breve della route...
            if (shortName == null) continue;
            if (!shortName.toLowerCase().contains(query.toLowerCase())) continue;           // ...Se non corrisponde alla query salta la route...

            List<Trip> tripsForRoute = dataIndexer.getTripsByRoute(routeId);                // ...Ottiene tutte le corse della linea corrente...
            if (tripsForRoute.isEmpty()) continue;                                          // ...Se non ci sono corse salta la route...

            Map<Integer, String> directions = new HashMap<>();                              // ...Costruisce la mappa direzione - capolinea...
            for (Trip trip : tripsForRoute) {                                               // ...Per ogni corsa della linea...
                if (trip.getTripHeadsign() != null) {                                       // ...Se la destinazione è valida...
                    directions.putIfAbsent(trip.getDirectionId(), trip.getTripHeadsign());  // ...Inserisce nella mappa direzione - capolinea della corsa.
                }
            }

            for (Map.Entry<Integer, String> dir : directions.entrySet()) {                   // Per ogni coppia della mappa direzione - capolinea...
                String directionId = String.valueOf(dir.getKey());                           // ...Estrae l'id della direzione...
                String directionName = dir.getValue();                                       // ...Estrae la destinazione...
                String suggestion = shortName + " → " + directionName + " [" +
                        route.getRouteId() + "|" + directionId + "]";                        // ...Formatta la riga di suggerimento...
                suggestions.add(suggestion);                                                 // ...Aggiunge la riga alla lista dei suggerimenti.
            }
        }
        return suggestions;                                                                  // Restituisce la lista di linee della tendina dei suggerimenti.
    }

    // RESTITUISCE LA PRIMA CORSA DI UNA LINEA -------------------------------------------------------------------------
    /**
     * Restituisce la prima corsa disponibile di una linea specifica per una data direzione.
     *
     * @param routeId     ID della route
     * @param directionId ID della direzione (
     * @return oggetto {@link Trip}
     */
    public Trip getFirstTrip(String routeId, int directionId) {
        List<Trip> trips = dataIndexer.getTripsByRouteAndDirection(routeId, directionId);
        return trips.stream().findFirst().orElse(null);   // Prende la prima corsa se esiste
    }

    // RESTITUISCE UNA LINEA IN BASE ALL'ID ----------------------------------------------------------------------------
    /**
     * Restituisce una route in base al suo ID.
     *
     * @param routeId ID della linea
     * @return        oggetto {@link Route}
     */
    public Route getRouteById(String routeId) {
        return dataIndexer.getRouteById(routeId);
    }

}







