package controller;

// Model.
import model.Route;
import model.Stop;
import model.Trip;

// Operator.
import operator.GTFSDataIndexer;

// Service.
import service.FavoritesService;
import service.ConnectivityService;

// Altre classi.
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Controller per la gestione della toolbar dell'applicazione.
 */
public class ToolBarController {

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private final GTFSDataIndexer dataIndexer;                 // GTFS statici indicizzati.

    // SERVICE ---------------------------------------------------------------------------------------------------------
    private final FavoritesService favoritesService;           // Classe service per la gestione dei preferiti.
    private final ConnectivityService connectivityService;     // Classe service per il monitoraggio del servizio realtime.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private String currentUser;                                // Utente attualmente loggato.
    private ConnectivityListener connectivityListener;         // Listener per la notifica del cambio di connessione.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param dataIndexer         operator contenente i dati GTFS indicizzati
     * @param favoritesService    service per la gestione dei preferiti
     * @param connectivityService service per il monitoraggio della connessione realtime
     */
    public ToolBarController(GTFSDataIndexer dataIndexer,
                             FavoritesService favoritesService,
                             ConnectivityService connectivityService) {
        this.dataIndexer = dataIndexer;
        this.favoritesService = favoritesService;
        this.connectivityService = connectivityService;

        // Registra un listener al connectivityService.
        connectivityService.setOnConnectionChange(this::onConnectionChange);
    }

    // REGISTRA IL LISTENER DELLA CLASSE AL SERVICE --------------------------------------------------------------------
    /**
     * Registra il listener della view per ricevere notifiche sui cambiamenti di connessione.
     *
     * @param listener listener da notificare in caso di cambio connessione
     */
    public void setConnectivityListener(ConnectivityListener listener) {
        this.connectivityListener = listener;                               // Salva il listener.
        boolean isOnline = connectivityService.isCurrentlyOnline();         // Chiede lo stato attuale della connessione al service.
        listener.onConnectionChanged(isOnline);                             // Chiama onConnectionChange() per la notifica dello stato.
    }

    // RICEVE LA NOTIFICA DEL CAMBIAMENTO DI STATO DAL SERVICE E LA INOLTRA ALLA VIEW ----------------------------------
    /**
     * Inoltra alla view il cambiamento di stato della connessione.
     *
     * @param isOnline true se la connessione è attiva, false altrimenti
     */
    private void onConnectionChange(boolean isOnline) {
        if (connectivityListener != null) {
            connectivityListener.onConnectionChanged(isOnline);
        }
    }

    // INTERFACCIA IMPLEMENTATA DALLA VIEW PER IL CAMBIAMENTO DI STATO -------------------------------------------------
    /**
     * Listener per ricevere notifiche sullo stato della connessione.
     */
    public interface ConnectivityListener {
        void onConnectionChanged(boolean isOnline);
    }

    // AGGIORNA LA LISTA ORDINATA DELLE FERMATE PREFERITE --------------------------------------------------------------
    /**
     * Restituisce la lista delle fermate preferite in base al nome.
     *
     * @return lista di stringhe formattate "[stop_id] nome fermata"
     */
    public List<String> updateStopFavoritesList() {
        Set<String> favoriteStopIds = favoritesService.getFavoriteStops(currentUser);                   // Riceve l'insieme dei preferiti per l'utente.
        List<String> sortedFavoritesList = new ArrayList<>();                                           // Crea la lista dei preferiti da mostrare.
        for (String stopId : favoriteStopIds) {                                                         // Per ogni stop_id dell'insieme...
            Stop stop = dataIndexer.getStopById(stopId);                                                // ...Recupera lo stop tramite indexer...
            if (stop != null) {
                sortedFavoritesList.add("[" + stop.getStopId() + "] " + stop.getStopName());            // ...E aggiunge "[stop_id] nome fermata" alla lista.
            }
        }
        Collections.sort(sortedFavoritesList);                                                          // Ordina la lista.
        return sortedFavoritesList;
    }

    // AGGIORNA LA LISTA ORDINATA DELLE LINEE PREFERITE ----------------------------------------------------------------
    /**
     * Restituisce la lista delle linee preferite ordinate.
     *
     * @return lista di stringhe formattate "[route_id] destinazione (directionId)"
     */
    public List<String> updateLineFavoritesList() {
        Set<String> favoriteLineIds = favoritesService.getFavoriteLines(currentUser);                   // Riceve l'insieme dei preferiti per l'utente.
        List<String> sortedFavoritesList = new ArrayList<>();                                           // Crea la lista dei preferiti da mostrare.

        for (String lineId : favoriteLineIds) {                                                         // Per ogni entry dell'insieme...
            String[] parts = lineId.split("_");
            if (parts.length != 2) continue;
            String routeId = parts[0];
            int directionId = Integer.parseInt(parts[1]);

            List<Trip> trips = dataIndexer.getTripsByRouteAndDirection(routeId, directionId);           // ...Recupera le corse tramite indexer...
            if (!trips.isEmpty()) {
                Trip trip = trips.get(0);                                                               // Usa una corsa come rappresentativa.
                sortedFavoritesList.add(
                        "[" + trip.getRouteId() + "] " + trip.getTripHeadsign() + " (" + trip.getDirectionId() + ")" );
            }
        }

        Collections.sort(sortedFavoritesList);                                                          // Ordina la lista.
        return sortedFavoritesList;
    }


    // SETTA L'UTENTE ATTUALMENTE LOGGATO -------------------------------------------------------------------------------
    /**
     * Imposta l'utente attualmente loggato e carica i preferiti.
     *
     * @param currentUser username dell'utente
     */
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        favoritesService.loadFavorites(currentUser);                                                    // Passa l'utente corrente al service.
    }

    // RESTITUISCE LA FERMATA TRAMITE ID -------------------------------------------------------------------------------
    /**
     * Restituisce la fermata in base all'ID.
     *
     * @param stopId ID della fermata
     * @return       oggetto {@link Stop} associato all'ID
     */
    public Stop getStopById(String stopId) {
        return dataIndexer.getStopById(stopId);
    }

    // RESTITUISCE LA LINEA TRAMITE ID ---------------------------------------------------------------------------------
    /**
     * Restituisce la linea in base all'ID.
     *
     * @param routeId ID della linea
     * @return        oggetto {@link Route} associato all'ID
     */
    public Route getRouteById(String routeId) {
        return dataIndexer.getRouteById(routeId);
    }

    // RESTITUISCE LA CORSA TRAMITE CAPOLINEA --------------------------------------------------------------------------
    /**
     * Restituisce la lista delle corse in base a linea e direzione.
     *
     * @param routeId     ID della linea
     * @param directionId ID della direzione
     * @return            lista di {@link Trip}
     */
    public List<Trip> getTripsByRouteAndDirection(String routeId, int directionId) {
        return dataIndexer.getTripsByRouteAndDirection(routeId, directionId);
    }
}

