package controller;

// Model.
import model.*;

// Operator.
import operator.GTFSDataIndexer;

// Service.
import service.FavoritesService;
import service.TimetableService;

// JavaFx.
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;

// Altre classi.
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller per la gestione delle informazioni delle fermate.
 */
public class StopInfoController {

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private final GTFSDataIndexer dataIndexer;                // GTFS statici indicizzati.

    // SERVICE ---------------------------------------------------------------------------------------------------------
    private final FavoritesService favoritesService;          // Classe service per la gestione dei preferiti.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private String currentUser;                               // Utente attualmente loggato.
    private Stop currentStop;                                 // Fermata attualmente mostrata nel pannello informativo.
    private LocalTime selectedTime;                           // Orario scelto dall’utente.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param dataIndexer      operator contenente i dati GTFS indicizzati
     * @param favoritesService service per la gestione dei preferiti
     */
    public StopInfoController(GTFSDataIndexer dataIndexer,
                              FavoritesService favoritesService) {
        this.dataIndexer = dataIndexer;
        this.favoritesService = favoritesService;
        this.selectedTime = LocalTime.now();
    }

    // AGGIORNA IL PANNELLO CON LA FERMATA SELEZIONATA -----------------------------------------------------------------
    /**
     * Aggiorna il pannello informativo con la fermata selezionata.
     *
     * @param stop          fermata selezionata
     * @param stopNameLabel label per il nome della fermata
     * @param arrivalsList  listView per le linee in arrivo
     */
    public void selectStop(Stop stop,
                           Label stopNameLabel,
                           ListView<Arrival> arrivalsList) {

        this.currentStop = stop;                                                // Fermata selezionata nella barra di ricerca.
        stopNameLabel.setText(stop.getStopName());                              // Imposta il nome della fermata sul pannello.
        List<Arrival> arrivals = getArrivalsForStop(stop);                      // Recupera le linee in arrivo per quella fermata.
        arrivalsList.setItems(FXCollections.observableArrayList(arrivals));     // Mostra gli arrivi nella listView.
    }

    // AGGIORNA LO STATO DI PREFERITO DELLA FERMATA CORRENTE -----------------------------------------------------------
    /**
     * Aggiunge o rimuove la fermata corrente dai preferiti dell'utente.
     *
     * @return true se la fermata è ora nei preferiti, false altrimenti
     */
    public boolean toggleFavoriteForCurrentStop() {
        if (currentStop == null) return false;
        favoritesService.toggleFavoriteStop(currentUser, currentStop.getStopId());
        return favoritesService.isStopFavorite(currentUser, currentStop.getStopId());
    }

    // SETTA L'UTENTE ATTUALMENTE LOGGATO ------------------------------------------------------------------------------
    /**
     * Imposta l'utente attualmente loggato e carica i suoi preferiti.
     *
     * @param currentUser username dell'utente
     */
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        favoritesService.loadFavorites(currentUser);
    }

    // SETTA L'ORARIO SCELTO DALL'UTENTE -------------------------------------------------------------------------------
    /**
     * Imposta l'orario scelto dall'utente per filtrare gli arrivi.
     *
     * @param time orario scelto
     */
    public void setSelectedTime(LocalTime time) {
        this.selectedTime = time;
    }

    // RESTITUISCE UNA LINEA TRAMITE ID --------------------------------------------------------------------------------
    /**
     * Restituisce la linea in base all'ID.
     *
     * @param routeId ID della linea
     * @return        oggetto {@link Route}
     */
    public Route getRouteById(String routeId) {
        return dataIndexer.getRouteById(routeId);
    }

    // RESTITUISCE UNA FERMATA TRAMITE ID ------------------------------------------------------------------------------
    /**
     * Restituisce la fermata in base all'ID.
     *
     * @param stopId ID della fermata
     * @return        oggetto {@link Stop}
     */
    public Stop getStopById(String stopId) {
        return dataIndexer.getStopById(stopId);
    }

    // RESTITUISCE UNA CORSA TRAMITE ID --------------------------------------------------------------------------------
    /**
     * Restituisce la corsa in base all'ID.
     *
     * @param tripId ID della corsa
     * @return        oggetto {@link Trip}
     */
    public Trip getTripById(String tripId) {
        return dataIndexer.getTripById(tripId);
    }

    // RESTITUISCE TUTTE LE CORSE IN ARRIVO ALLA FERMATA DA SELECTEDTIME A SELECTEDTIME + 1 ORA ------------------------
    /**
     * Restituisce tutte le corse in arrivo alla fermata nel range di 1 ora
     * a partire dall'orario selezionato.
     *
     * @param selectedStop fermata
     * @return             lista di corse come oggetti {@link Arrival}
     */
    private List<Arrival> getArrivalsForStop(Stop selectedStop) {
        String stopId = selectedStop.getStopId();                                                                           // Recupera l'ID della fermata selezionata.
        List<StopTime> stopTimes = dataIndexer.getStopTimesByStop(stopId);                                                  // Recupera la lista di stoptime della fermata tramite l'ID.
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), selectedTime);                                              // Calcola l'orario di inizio del range di arrivi (seleione della fermata).
        LocalDateTime end = start.plusHours(1);                                                                             // Calcola l'orario di fine del range di arrivi (dopo 1 ora dalla selezione).

        // Tentativo realtime ------------------------------------------------------------------------------------------
        try {
            List<GTFSDataIndexer.PredictedArrival> predictions = dataIndexer.getPredictedArrivals(stopId, null);     // Recupera tutte le predizioni di arrivo per la fermata
            if (predictions != null && !predictions.isEmpty()) {                                                            // Se ci sono predizioni...
                List<Arrival> realtimeList = predictions.stream()                                                           // ...Avvia la traformazione delle predizioni realtime in oggetti Arrival.

                        .filter(predictedArrival -> {                                                         // Filtra le predizioni.
                            LocalDateTime predicted = Instant.ofEpochSecond(predictedArrival.arrivalEpochSec)               // Converte l'epoch in LocaDateTime...
                                    .atZone(ZoneId.of("Europe/Rome"))                                                // ...Con fuso orario di Roma.
                                    .toLocalDateTime();
                            return !predicted.isBefore(start) && !predicted.isAfter(end);                                   // Prende solo le predizioni nel range start - end (1 ora).
                        })

                        .sorted(Comparator.comparingLong(predictedArrival
                                -> predictedArrival.arrivalEpochSec))                                                       // Ordina le predizioni in base al timestamp di arrivo in ordine crescente.

                        .map(predictedArrival -> {                                                            // Mappa ogni predizione realtime con l'Arrival corrispondente.
                            Trip trip = dataIndexer.getTripById(predictedArrival.tripId);                                   // Recupera la corsa corrispondente alla predizione tramite ID.
                            if (trip == null) return null;                                                                  // Se la corsa non è valida scarta il record per questa predizione.
                            Route route = dataIndexer.getRouteById(trip.getRouteId());                                      // Recupera la linea corrispondente alla corsa tramite ID.
                            if (route == null) return null;                                                                 // Se la linea non è valida scarta il record per questa predizione.
                            if (trip.getTripHeadsign().equalsIgnoreCase(selectedStop.getStopName())) return null;           // Se la fermata corrisponde con il capolinea della corsa scarta il record per questa predizione.
                            LocalDateTime predicted = Instant.ofEpochSecond(predictedArrival.arrivalEpochSec)               // Converte l'epoch in LocaDateTime...
                                    .atZone(ZoneId.of("Europe/Rome"))                                                // ...Con fuso orario di Roma.
                                    .toLocalDateTime();

                            // Recupero orario statico.
                            StopTime stopTime = dataIndexer.getStopTimesByTrip(trip.getTripId())                            // Recupera la lista di stopTimes statici per la corsa.
                                    .stream()                                                                               // Trasforma la lista in uno stream.
                                    .filter(st -> st.getStopId().equals(stopId))                                   // Filtra gli stopTime per ID della fermata.
                                    .findFirst().orElse(null);                                                        // Prende il primo stopTime trovato per la fermata.
                            LocalDateTime scheduled = null;                                                                 // Orario statico di arrivo alla fermata.
                            if (stopTime != null) {                                                                         // Se lo stopTime è valido...
                                scheduled = TimetableService.parseArrivalTimeSafe(stopTime.getArrivalTime());               // ...Orario statico = orario dello stopTime parsato con TimetableService.
                            }
                            String formattedTime = scheduled == null ? "--:--" :
                                    String.format("%02d:%02d", scheduled.getHour(), scheduled.getMinute());                 // Orario statico da mostrare: HH:mm o --:-- se non valido.

                            // Calcolo delay.
                            Long delayMinutes = null;                                                                       // Minuti di ritardo.
                            if (scheduled != null && predicted != null) {                                                   // Se l'orario statico è valido e la predizione realtime è valida...
                                delayMinutes = Duration.between(scheduled, predicted).toMinutes();                          // Calcola il delay tra i due orari.
                            }

                            return new Arrival(                                                                             // Restituisce l'Arrival con...
                                    route.getRouteShortName(),                                                              // ...Nome linea...
                                    trip.getTripHeadsign(),                                                                 // ...Destinazione...
                                    formattedTime,                                                                          // ...Orario statico...
                                    true,                                                                                   // ...Indicatore realtime...
                                    null,                                                                                   // ...Stato (NEXT, FUTURE, PAST)...
                                    delayMinutes,                                                                           // ...Minuti di ritardo/anticipo...
                                    stopId,                                                                                 // ...ID della fermata corrispondente...
                                    trip.getTripId());                                                                      // ...ID della corsa corrispondente.
                        })
                        .filter(Objects::nonNull)                                                                           // Rimuove gli elementi scartati in map.
                        .distinct()                                                                                         // Rimuove gli elementi duplicati.
                        .collect(Collectors.toList());                                                                      // Materializza lo stream in una lista.

                if (!realtimeList.isEmpty()) {                                                                              // Se ci sono predizioni realtime nel range di 1 ora...
                    return realtimeList;                                                                                    // ...Restituisce la lista di Arrival realtime.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback su dati statici ------------------------------------------------------------------------------------
        List<Arrival> staticList = stopTimes.stream()                                                                       // Avvia la traformazione delle predizioni statiche in oggetti Arrival.

                .filter(stopTime -> {
                    LocalDateTime predicted = TimetableService.parseArrivalTimeSafe(stopTime.getArrivalTime());             // Converte l'arrivalTime dello stopTime in un LocalDateTime.
                    return predicted != null && !predicted.isBefore(start) && !predicted.isAfter(end);                      // Prende solo le predizioni nel range start - end (1 ora).
                })

                .sorted(Comparator.comparing(StopTime::getArrivalTime))                                                     // Ordina per arrivalTime in ordine crescente.

                .map(stopTime -> {                                                                                 // Mappa ogni predizione statica con l'Arrival corrispondente.
                    Trip trip = dataIndexer.getTripById(stopTime.getTripId());                                              // Recupera la corsa associata allo stopTime tramite ID.
                    if (trip == null) return null;                                                                          // Se la corsa non è valida scarta il record per questa predizione.
                    Route route = dataIndexer.getRouteById(trip.getRouteId());                                              // Recupera la linea associata allo corsa tramite ID.
                    if (route == null) return null;                                                                         // Se la linea non è valida scarta il record per questa predizione.
                    if (trip.getTripHeadsign().equalsIgnoreCase(selectedStop.getStopName())) return null;                   // Se la fermata corrisponde con il capolinea della corsa scarta il record per questa predizione.

                    LocalDateTime scheduled = TimetableService.parseArrivalTimeSafe(stopTime.getArrivalTime());             // Orario statico di arrivo alla fermata parsato con TimetableService.
                    String formattedTime = scheduled == null ? "--:--" :
                            String.format("%02d:%02d", scheduled.getHour(), scheduled.getMinute());                         // Orario statico da mostrare: HH:mm o --:-- se non valido.

                    return new Arrival(                                                                                     // Restituisce l'Arrival con...
                            route.getRouteShortName(),                                                                      // ...Nome linea...
                            trip.getTripHeadsign(),                                                                         // ...Destinazione...
                            formattedTime,                                                                                  // ...Orario statico...
                            false,                                                                                          // ...Indicatore realtime...
                            null,                                                                                           // ...Stato (NEXT, FUTURE, PAST)...
                            null,                                                                                           // ...Minuti di ritardo/anticipo...
                            stopId,                                                                                         // ...ID della fermata corrispondente...
                            trip.getTripId());                                                                              // ...ID della corsa corrispondente.
                })
                .filter(Objects::nonNull)                                                                                   // Rimuove gli elementi scartati in map.
                .distinct()                                                                                                 // Rimuove gli elementi duplicati.
                .collect(Collectors.toList());                                                                              // Materializza lo stream in una lista.

        return staticList;                                                                                                  // Restituisce la lista di Arrival statici.
    }

    // RESTITUISCE LA FERMATA ATTUALMENTE VISUALIZZATA -----------------------------------------------------------------
    /**
     * Restituisce la fermata attualmente visualizzata nel pannello informativo.
     *
     * @return oggetto {@link Stop}
     */
    public Stop getCurrentStop() {
        return currentStop;
    }

    // RESTITUISCE SE LA FERMATA CORRENTE È NEI PREFERITI --------------------------------------------------------------
    /**
     * Restituisce true se la fermata corrente è nei preferiti dell'utente.
     *
     * @return true se nei preferiti, false altrimenti
     */
    public boolean isCurrentStopFavorite() {
        return currentStop != null && favoritesService.isStopFavorite(currentUser, currentStop.getStopId());
    }
}


