package view;

// Model.
import model.Arrival;
import model.Route;
import model.Stop;
import model.Trip;

// Controller.
import controller.StopInfoController;

// Layout e nodi.
import view.layouts.StopInfoLayout;
import view.nodes.StopInfoNode;

// Classi javaFX necessarie per mostrare la GUI.
import javafx.stage.Popup;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollBar;
import javafx.geometry.Bounds;
import javafx.application.Platform;

// Altre classi.
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe view per la zona informativa delle fermate.
 * <p>
 * Compone la GUI della zona informativa delle fermate.
 */
public class StopInfoView {

    // ROOT ------------------------------------------------------------------------------------------------------------
    private BorderPane stopInfoRoot;                            // Layout del pannello di informazione delle fermate.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final StopInfoNode stopInfoNode;                    // Nodi per il pannello informativo delle fermate.
    private final StopInfoLayout stopInfoLayout;                // Layout del pannello informativo delle fermate.

    // CONTROLLER ------------------------------------------------------------------------------------------------------
    private final StopInfoController stopInfoController;        // Controller del pannello di informazione delle fermate.

    // VIEW ------------------------------------------------------------------------------------------------------------
    private LineInfoView lineInfoView;                          // Riferimento al pannello informativo delle linee.
    private MapsView mapsView;                                  // Riferimento alla mappa.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param stopInfoController controller del pannello informativo delle fermate.
     */
    public StopInfoView(StopInfoController stopInfoController) {
        this.stopInfoController = stopInfoController;
        this.stopInfoNode = new StopInfoNode();
        this.stopInfoLayout = new StopInfoLayout();
    }

    // CREAZIONE DELLA UI ----------------------------------------------------------------------------------------------
    /**
     * Crea l'interfaccia della zona informativa delle fermate, assembla tutti i layout e le view.
     */
    private void createView() {

        // Etichetta per il nome della fermata.
        Label stopNameLabel = stopInfoNode.createStopNameLabel();
        stopNameLabel.setText("Linee in arrivo alla fermata...");

        // Bottone per la selezione delle fermate preferite.
        Button favoriteButton = stopInfoNode.createFavoriteButton();
        favoriteButton.setOnAction(_ -> {
            stopInfoController.toggleFavoriteForCurrentStop();                                                              // Notifica il controller che è stato cliccato.
            boolean isFav = stopInfoController.isCurrentStopFavorite();                                                     // Verifica lo stato aggiornato.
            stopInfoNode.setFavoriteButtonState(favoriteButton, isFav);                                                     // Aggiorna l’icona (colore).
        });

        // Lista delle corse in arrivo alla fermata (Arrival).
        ListView<Arrival> arrivalsList = stopInfoNode.createArrivalsList();
        arrivalsList.setOnMouseClicked(_ -> {                                                                    // Quando si seleziona una riga della listView.
            Arrival selected = arrivalsList.getSelectionModel().getSelectedItem();                                          // Recupera l'Arrival selezionato.
            if (selected != null) {                                                                                         // Se l'Arrival è valido...
                Stop stop = stopInfoController.getStopById(selected.getStopId());                                           // ...Recupera la fermata corrispondente tramite ID.
                if (stop != null) {
                    updateCurrentStop(stop);                                                                                // ...Aggiorna la view con la fermata selezionata.
                }

                Trip selectedTrip = stopInfoController.getTripById(selected.getTripId());                                   // Recupera la corsa corrispondente.
                if (selectedTrip != null) {
                    Route route = stopInfoController.getRouteById(selected.getRouteId());                                   // Recupera la linea corrispondente.
                    if (lineInfoView != null) {
                        lineInfoView.updateCurrentLine(route, selectedTrip);                                                // ...Aggiorna la LineInfoView con la linea selezionata.
                    }
                    if (mapsView != null) {
                        mapsView.showLineOnMap(selectedTrip);                                                               // ...Mostra la linea sulla mappa.
                    }
                }
            }
        });

        // Bottone per la selezione dell'orario di partenza.
        Button selectionTimeButton = stopInfoNode.createSelectionTimeButton();

        // Lista degli orari di partenza.
        ListView<String> timesList = stopInfoNode.createTimesList();
        List<String> times = new ArrayList<>();                                                                             // Lista contenente gli orari.
        for (int h = 0; h < 24; h++) {                                                                                      // Imposta gli orari dalle 00 alle 23...
            times.add(String.format("%02d:00", h));                                                                         // ...Nel formato HH.00...
            times.add(String.format("%02d:30", h));                                                                         // ...Ogni mezz'ora per più precisione (HH.30).
        }
        timesList.getItems().addAll(times);                                                                                 // Aggiungo gli orari a timesList.

        // Popup per la lista degli orari di partenza.
        Popup timesPopup = new Popup();
        timesPopup.setAutoHide(true);                                                                                       // Chiude automaticamente se clicchi fuori.
        timesPopup.getContent().add(timesList);                                                                             // Aggiunge la lista degli orari al popup.

        // Mostra/nasconde popup cliccando sul bottone.
        selectionTimeButton.setOnAction(_ -> {
            if (!timesPopup.isShowing()) {                                                                                  // Se il popup al momento del click del bottone non è visibile.
                Bounds bounds = selectionTimeButton.localToScreen(selectionTimeButton.getBoundsInLocal());                  // Calcola le coordinate del bottone sullo schermo.
                double popupX = bounds.getMinX();                                                                           // Allineato orizzontalmente al bottone.
                double popupY = bounds.getMaxY() + 5;                                                                       // Sotto al bottone distanziato di 5 pixel.
                timesPopup.show(selectionTimeButton, popupX, popupY);                                                       // Mostra il popup alle coordinate scelte.

                // Toglie la barra di scorrimento verticale.
                removeScrollBar(timesList);
            } else {                                                                                                        // Se il popup al momento del click del bottone è visibile...
                timesPopup.hide();                                                                                          // ...Viene nascosto.
            }
        });

        // Selezione dell'orario dalla ListView.
        timesList.setOnMouseClicked(_ -> {
            String selected = timesList.getSelectionModel().getSelectedItem();                                              // Recupera l'orario selezionato dalla lista degli orari...
            if (selected != null) {                                                                                         // ...Se è valido...
                selectionTimeButton.setText(selected);                                                                      // ...Il bottone mostra l'orario selezionato...
                stopInfoController.setSelectedTime(LocalTime.parse(selected));                                              // ...L'orario viene passato al controller sottoforma di oggetto LocalTime...
                timesPopup.hide();                                                                                          // ...Il popup viene nascosto...
                refreshCurrentStop();                                                                                       // ...Aggiorna il pannello in base al nuovo orario selezionato.
            }
        });

        // Bottone per la selezione dell'orario attuale.
        Button nowButton = stopInfoNode.createNowButton();
        nowButton.setOnAction(_ -> {
            LocalTime now = LocalTime.now();                                                                                // Salva l'orario corrente dal sistema...
            String selectedTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));                                         // ...Imposto l'orario nella stringa del formato HH:mm...
            selectionTimeButton.setText(selectedTime);                                                                      // ...Imposta l'orario sul selectionTimeButton...
            stopInfoController.setSelectedTime(now);                                                                        // ...Passa al controller l'orario selezionato...
            refreshCurrentStop();                                                                                           // ...Aggiorna il pannello in base al nuovo orario selezionato.
        });

        // ASSEMBLAGGIO DEL LAYOUT ------------------------------------------------------------------------------------
        HBox stopTitleBox = stopInfoLayout.createStopTitleBox(favoriteButton, stopNameLabel);
        HBox selectionTimeBox = stopInfoLayout.createSelectionTimeBox(selectionTimeButton, nowButton);
        VBox topBox = stopInfoLayout.createTopBox(stopTitleBox, selectionTimeBox);

        stopInfoRoot = stopInfoLayout.createStopInfoRoot();
        stopInfoRoot.setTop(topBox);
        stopInfoRoot.setCenter(arrivalsList);
    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Restituisce la zona informativa delle fermate.
     * Crea la view se non è stata ancora inizializzata.
     *
     * @return BorderPane contenente la view.
     */
    public BorderPane getView() {       // Chiamata in HomeLayout.
        if (stopInfoRoot == null) {     // Se il layout non è stato creato...
            createView();               // ...Lo crea.
        }
        return stopInfoRoot;
    }

    // AGGIORNA IL PANNELLO INFORMATIVO TRAMITE CASTING ----------------------------------------------------------------
    /**
     * Aggiorna il pannello informativo per la fermata selezionata.
     *
     * @param stop fermata corrente.
     */
    private void updateStopInfoView(Stop stop) {
        VBox topBox = (VBox) stopInfoRoot.getTop();                                             // Recupera il layout della zona superiore del pannello.
        HBox titleHBox = (HBox) topBox.getChildren().get(0);                                    // Recupera il layout della zona del nome della fermata.
        Label stopNameLabel = (Label) titleHBox.getChildren().get(1);                           // Recupera l'etichetta del nome della fermata.
        Button favoriteButton = (Button) titleHBox.getChildren().get(0);                        // Recupera il bottone dei preferiti.
        ListView<Arrival> arrivalsList = (ListView<Arrival>) stopInfoRoot.getCenter();          // Recupera la lista delle fermate della linea.

        stopInfoController.selectStop(stop, stopNameLabel, arrivalsList);                       // Chiama il controller per aggiornare la lista delle corse in arrivo.
        boolean isFav = stopInfoController.isCurrentStopFavorite();                             // Chiama il controller per vedere se la fermata è tra i preferiti (true) o no (false).
        stopInfoNode.setFavoriteButtonState(favoriteButton, isFav);                             // Cambia il colore del bottone dei preferiti in base a isFav.
    }

    // AGGIORNAMENTO DEL PANNELLO QUANDO SI CAMBIA LA FERMATA ----------------------------------------------------------
    /**
     * Aggiorna il pannello quando viene selezionata una nuova fermata.
     *
     * @param stop fermata selezionata.
     */
    public void updateCurrentStop(Stop stop) {
        if (stopInfoRoot == null) createView();
        updateStopInfoView(stop);
    }

    // AGGIORNAMENTO DEL PANNELLO QUANDO SI CAMBIA L'ORARIO ------------------------------------------------------------
    /**
     * Aggiorna il pannello per il nuovo orario selezionato.
     */
    private void refreshCurrentStop() {
        if (stopInfoRoot == null) return;
        Stop current = stopInfoController.getCurrentStop();
        if (current != null) updateStopInfoView(current);
    }

    // SETTER DI COLLEGAMENTO CON MAPSVIEW -----------------------------------------------------------------------------
    /**
     * Imposta il riferimento alla {@link MapsView}.
     *
     * @param mapsView {@link MapsView} da collegare.
     */
    public void setMapsView(MapsView mapsView) {
        this.mapsView = mapsView;
    }

    // SETTER DI COLLEGAMENTO CON LINEINFOVIEW -------------------------------------------------------------------------
    /**
     * Imposta il riferimento al pannello informativo delle linee.
     *
     * @param lineInfoView LineInfoView collegata.
     */
    public void setLineInfoView(LineInfoView lineInfoView) {
        this.lineInfoView = lineInfoView;
    }

    // RIMUOVE LA SCROLLBAR DALLA LISTVIEW ---------------------------------------------------------------------------
    /**
     * Rimuove la scrollbar verticale dalla ListView per motivi estetici.
     *
     * @param listView ListView da cui rimuovere la scrollbar.
     */
    private void removeScrollBar(ListView<?> listView) {
        Platform.runLater(() -> {
            ScrollBar vBar = (ScrollBar) listView.lookup(".scroll-bar:vertical");
            if (vBar != null) {
                vBar.setStyle(
                        "-fx-background-color: TRANSPARENT;" +
                        "-fx-pref-width: 0;" +
                        "-fx-opacity: 0;"
                );
            }
        });
    }
}


