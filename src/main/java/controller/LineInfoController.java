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

// Altre classi/*/
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Controller per la gestione delle informazioni di una linea.
 */
public class LineInfoController {

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private final GTFSDataIndexer dataIndexer;              // GTFS statici indicizzati.

    // SERVICE ---------------------------------------------------------------------------------------------------------
    private final FavoritesService favoritesService;        // Classe service per la gestione dei preferiti.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private String currentUser;                             // Utente attualmente loggato.
    private Route currentRoute;                             // Linea attualmente mostrata nel pannello informativo.
    private Trip currentTrip;                               // Corsa attualmente mostrata nel pannello informativo.
    private LocalTime selectedTime;                         // Orario scelto dall’utente.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param dataIndexer      operator contenente i dati GTFS indicizzati
     * @param favoritesService service per la gestione dei preferiti
     */
    public LineInfoController(GTFSDataIndexer dataIndexer,
                              FavoritesService favoritesService) {
        this.dataIndexer = dataIndexer;
        this.favoritesService = favoritesService;
        this.selectedTime = LocalTime.now();
    }

// AGGIORNA IL PANNELLO CON LA LINEA SELEZIONATA -------------------------------------------------------------------
    /**
     * Aggiorna il pannello informativo con la linea e corsa selezionate.
     *
     * @param route         linea
     * @param trip          corsa
     * @param lineNameLabel label per il nome della linea
     * @param stopsList     listView per le fermate della corsa
     */
    public void selectLine(Route route,
                           Trip trip,
                           Label lineNameLabel,
                           ListView<Arrival> stopsList) {

        this.currentRoute = route;                                                              // Linea selezionata nella barra di ricerca.
        this.currentTrip = trip;                                                                // Corsa selezionata nella barra di ricerca.
        lineNameLabel.setText(route.getRouteShortName() + " - " + trip.getTripHeadsign());      // Imposta il nome della linea sul pannello.

        List<Arrival> stops = getStopsForLine(trip);                                            // Recupera le fermate per quella linea come oggetti Arrival.
        stopsList.setItems(FXCollections.observableArrayList(stops));                           // Mostra le fermate nella listView.
    }
    // AGGIORNA LO STATO DI PREFERITO DELLA LINEA CORRENTE ------------------------------------------------------------
    /**
     * Aggiunge o rimuove la linea corrente dai preferiti dell'utente.
     *
     * @return true se la linea è ora tra i preferiti, false altrimenti
     */
    public boolean toggleFavoriteForCurrentLine() {
        if (currentTrip == null) return false;
        favoritesService.toggleFavoriteLine(currentUser, currentTrip);
        return favoritesService.isLineFavorite(currentUser, currentTrip);
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
     * mposta l'orario scelto dall'utente per filtrare gli arrivi.
     *
     * @param time orario scelto
     */
    public void setSelectedTime(LocalTime time) {
        this.selectedTime = time;
    }

    // RESTITUISCE UNA FERMATA TRAMITE ID ------------------------------------------------------------------------------
    /**
     * Restituisce la fermata in base all'ID.
     *
     * @param stopId ID della linea
     * @return        oggetto {@link Stop}
     */
    public Stop getStopById(String stopId) {
        return dataIndexer.getStopById(stopId);
    }

    // RESTITUISCE TUTTE LE CORSE DI UNA LINEA IN UNA DIREZIONE --------------------------------------------------------
    /**
     * Restituisce tutte le corse di una linea in base alla direzione.
     *
     * @param routeId     ID della linea
     * @param directionId ID della direzione
     * @return            lista di {@link Trip}
     */
    public List<Trip> getTripsByRouteIdAndDirId(String routeId,
                                                String directionId) {
        int dirIdInt = Integer.parseInt(directionId.trim());
        return dataIndexer.getTripsByRouteAndDirection(routeId, dirIdInt);
    }

    // RESTITUISCE TUTTE LE FERMATE DELLA CORSA ATTUALE DELLA LINEA DA SELECTEDTIME FINO AL CAPOLINEA ------------------
    /**
     * Restituisce tutte le fermate della corsa attuale della linea,
     * a partire dall'orario selezionato fino al capolinea.
     *
     * @param selectedTrip corsa
     * @return             lista di fermate come oggetti {@link Arrival}
     */
    private List<Arrival> getStopsForLine(Trip selectedTrip) {
        String routeId = selectedTrip.getRouteId();                                                                         // Recupera l'ID della linea tramite la corsa.
        String directionId = String.valueOf(selectedTrip.getDirectionId());                                                 // Recupera la direzione della corsa.
        List<Trip> trips = getTripsByRouteIdAndDirId(routeId, directionId);                                                 // Recupera la lista di corse della stessa linea e con la stessa direzione.
        if (trips.isEmpty()) return Collections.emptyList();                                                                // Se non ci sono corse allora restituisce la lista vuota.

        LocalDate today = LocalDate.now();                                                                                  // Recupera la data di oggi.
        LocalDateTime now = LocalDateTime.of(today, selectedTime);                                                          // Recupera la LocalDateTime con data di oggi e orario selezionato.

        currentTrip = null;                                                                                                 // Corsa attualmente selezionata.
        LocalDateTime minFutureStart = null;                                                                                // Orario della corsa futura più vicina all'orario selezionato.

        // Scelta della corsa da mostrare ------------------------------------------------------------------------------
        for (Trip trip : trips) {                                                                                           // Per ogni corsa con linea e direzione uguali...
            List<StopTime> stopTimes = dataIndexer.getStopTimesByTrip(trip.getTripId());                                    // Recupera la lista di stopTime trammite l'ID della corsa.
            if (stopTimes == null || stopTimes.isEmpty()) continue;                                                         // Se la lista di stopTime non è valida salta la corsa.

            LocalDateTime firstStatic = TimetableService
                    .parseArrivalTimeSafe(stopTimes.get(0).getArrivalTime());                                               // Calcola l'orario statico della prima fermata con TimetableService.
            LocalDateTime lastStatic = TimetableService
                    .parseArrivalTimeSafe(stopTimes.get(stopTimes.size() - 1).getArrivalTime());                            // Calcola l'orario statico dell'ultima fermata con TimetableService.
            if (firstStatic == null || lastStatic == null) continue;                                                        // Se uno dei due orari non è valido salta la corsa.
            LocalDateTime firstEffective = firstStatic;                                                                     // Inizialmente:orario effettivo della prima fermata = orario statico della prima fermata.
            LocalDateTime lastEffective = lastStatic;                                                                       // Inizialmente: orario effettivo dell'ultima fermata = orario statico dell'ultima fermata.

            // Tentativo di aggiornamento realtime per la prima fermata ------------------------------------------------
            try {
                String firstStopId = stopTimes.get(0).getStopId();                                                          // Recupera la prima fermata della corsa.
                List<GTFSDataIndexer.PredictedArrival> predictionsFirst = dataIndexer
                        .getPredictedArrivals(firstStopId, trip.getRouteId());                                              // Recupera tutte le predizioni di arrivo per fermata.
                if (predictionsFirst != null) {                                                                             // Se ci sono predizioni...
                    for (GTFSDataIndexer.PredictedArrival predictedArrival : predictionsFirst) {                            // ...Per ogni predizione...
                        if (trip.getTripId().equals(predictedArrival.tripId)) {                                             // ...Se fa parte della corsa attuale...
                            firstEffective = Instant.ofEpochSecond(predictedArrival.arrivalEpochSec)                        // ...Aggiornamento: orario effettivo della prima fermata = orario realtime della prima fermata.
                                    .atZone(ZoneId.of("Europe/Rome"))
                                    .toLocalDateTime();
                            break;                                                                                          // Serve solo la predizione per la corsa scelta.
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Tentativo di aggiornamento realtime per l'ultima fermata ------------------------------------------------
            try {
                String lastStopId = stopTimes.get(stopTimes.size() - 1).getStopId();                                        // Recupera l'ultima fermata della corsa.
                List<GTFSDataIndexer.PredictedArrival> predictionsLast = dataIndexer
                        .getPredictedArrivals(lastStopId, trip.getRouteId());                                               // Recupera tutte le predizioni di arrivo per fermata.
                if (predictionsLast != null) {                                                                              // Se ci sono predizioni...
                    for (GTFSDataIndexer.PredictedArrival predictedArrival : predictionsLast) {                             // ...Per ogni predizione...
                        if (trip.getTripId().equals(predictedArrival.tripId)) {                                             // ...Se fa parte della corsa attuale...
                            lastEffective = Instant.ofEpochSecond(predictedArrival.arrivalEpochSec)                         // ...Aggiornamento: orario effettivo dell'ultima fermata = orario realtime dell'ultima fermata.
                                    .atZone(ZoneId.of("Europe/Rome"))
                                    .toLocalDateTime();
                            break;                                                                                          // Serve solo la predizione per la corsa scelta.
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (firstEffective == null || lastEffective == null) continue;                                                  // Se le predizioni non sono valide salta la corsa.

            if (!now.isBefore(firstEffective) && !now.isAfter(lastEffective)) {                                             // Se now è compreso tra prima fermata e ultima fermata, è dentro la corsa....
                currentTrip = trip;                                                                                         // ...Imposta la corsa come corsa corrente (visualizzata nel pannello)...
                break;                                                                                                      // Termina il ciclo.
            }

            if (now.isBefore(firstEffective)) {                                                                             // Se now è prima della partenza della corsa...
                if (minFutureStart == null || firstEffective.isBefore(minFutureStart)) {                                    // ...Se questa corsa parte prima della migliore futura trovata finora...
                    minFutureStart = firstEffective;                                                                        // Aggiorna il timestamp della prossima corsa futura più vicina.
                    currentTrip = trip;                                                                                     // Imposta la corsa come corsa corrente (visualizzata nel pannello).
                }
            }
        }
                                                                                                                            // Se now è dopo l'ultima fermata della corsa la corsa viene ignorata e si passa alla successiva.
        if (currentTrip == null) return Collections.emptyList();                                                            // Se non è stata trovata alcuna corsa restituisce una lista vuota.
        List<StopTime> activeStopTimes = dataIndexer.getStopTimesByTrip(currentTrip.getTripId());                           // Altrimenti recupera gli stopTime della corsa attuale.

        // Restituzione delle fermate con orari ------------------------------------------------------------------------
        AtomicBoolean foundNext = new AtomicBoolean(false);                                                       // Flag per marcare la prima fermata come NEXT (AtomicBoolean = boolean mutabile).
        return activeStopTimes.stream()
                .map(stopTime -> {                                                                                 // Mappa ogni stopTime con l'Arrival corrispondente.
                    Stop stop = dataIndexer.getStopById(stopTime.getStopId());                                              // Recupera la fermata corrispondente allo stopTime tramite iD.
                    if (stop == null) return null;                                                                          // Se la fermata non è valida ignora questo record.
                    LocalDateTime scheduled = TimetableService.parseArrivalTimeSafe(stopTime.getArrivalTime());             // Orario statico alla fermata.
                    LocalDateTime predicted = null;                                                                         // Orario realtime alla fermata.
                    boolean realtime = false;                                                                               // Indicatore realtime.

                    // Tentativo di aggiornamento realtime per la fermata ----------------------------------------------
                    try {
                        List<GTFSDataIndexer.PredictedArrival> predictions = dataIndexer
                                .getPredictedArrivals(stopTime.getStopId(), currentTrip.getRouteId());                      // Recupera la lista di predizioni realtime per la fermata.
                        if (predictions != null) {                                                                          // Se la lista è valida...
                            for (GTFSDataIndexer.PredictedArrival predictedArrival : predictions) {                         // ...Per ogni predizione trovata...
                                if (currentTrip.getTripId().equals(predictedArrival.tripId)) {                              // ...Se la corsa delle predizioni corrisponde alla corsa attuale...
                                    predicted = Instant.ofEpochSecond(predictedArrival.arrivalEpochSec)                     // ...Aggiornamento: orario realtime = predizione realtime.
                                            .atZone(ZoneId.of("Europe/Rome"))
                                            .toLocalDateTime();
                                    realtime = true;                                                                        // Indicatore realtime true.
                                    break;                                                                                  // Serve solo la predizione corrispondente alla corsa corrente.
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String formattedTime = scheduled == null ? "--:--" :
                            String.format("%02d:%02d", scheduled.getHour(), scheduled.getMinute());                         // Orario statico da mostrare: HH:mm o --:-- se non valido.

                    // Calcolo delay.
                    Long delayMinutes = null;                                                                               // Minuti di ritardo.
                    if (realtime && scheduled != null && predicted != null) {                                               // Se l'orario statico è valido e la predizione realtime è valida...
                        delayMinutes = Duration.between(scheduled, predicted).toMinutes();                                  // Calcola il delay tra i due orari.
                    }

                    // Calcolo status.
                    Arrival.StopStatus status;                                                                              // Stato NEXT, FUTURE, PAST degli arrivi alle fermate
                    if (!foundNext.get() && scheduled != null && !scheduled.isBefore(now)) {                                // Prossima fermata in arrivo...
                        status = Arrival.StopStatus.NEXT;                                                                   // ...Stato = NEXT.
                        foundNext.set(true);                                                                                // Flag: NEXT gia trovato.
                    } else if (scheduled != null && scheduled.isBefore(now)) {                                              // Fermate passate...
                        status = Arrival.StopStatus.PAST;                                                                   // ...Stato = PAST.
                    } else {                                                                                                // Fermate future...
                        status = Arrival.StopStatus.FUTURE;                                                                 // Stato = FUTURE.
                    }

                    return new Arrival(                                                                                     // Restituisce l'Arrival con...
                            routeId,                                                                                        // ...Nome linea...
                            stop.getStopName(),                                                                             // ...Destinazione...
                            formattedTime,                                                                                  // ...Orario statico...
                            realtime,                                                                                       // ...Indicatore realtime...
                            status,                                                                                         // ...Stato (NEXT, FUTURE, PAST)...
                            delayMinutes,                                                                                   // ...Minuti di ritardo/anticipo...
                            stopTime.getStopId(),                                                                           // ...ID della fermata corrispondente...
                            currentTrip.getTripId());                                                                       // ...ID della corsa corrispondente.
                })
                .filter(Objects::nonNull)                                                                                   // Rimuove gli elementi scartati in map.
                .distinct()                                                                                                 // Rimuove gli elementi duplicati.
                .collect(Collectors.toList());                                                                              // Materializza lo stream in una lista.
    }

    // RESTITUISCE LA LINEA ATTUALMENTE VISUALIZZATA -------------------------------------------------------------------
    /**
     * Restituisce la linea attualmente mostrata nel pannello informativo.
     *
     * @return oggetto {@link Route}
     */
    public Route getCurrentRoute() {
        return currentRoute;
    }

    // RESTITUISCE LA CORSA ATTUALMENTE VISUALIZZATA -------------------------------------------------------------------
    /**
     * Restituisce la corsa attualmente mostrata nel pannello informativo.
     *
     * @return oggetto {@link Trip}
     */
    public Trip getCurrentTrip() {
        return currentTrip;
    }

    // RESTITUISCE SE LA LINEA CORRENTE È NEI PREFERITI ----------------------------------------------------------------
    /**
     * Verifica se la linea corrente è tra i preferiti dell'utente.
     *
     * @return true se nei preferiti, false altrimenti
     */
    public boolean isCurrentLineFavorite() {
        return currentTrip != null && favoritesService.isLineFavorite(currentUser, currentTrip);
    }
}







