package view;

// Model.
import model.Arrival;
import model.Route;
import model.Stop;
import model.Trip;

// Controller.
import controller.LineInfoController;

// Layout e nodi.
import view.layouts.LineInfoLayout;
import view.nodes.LineInfoNode;

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
 * Classe view per la zona informativa delle linee.
 * <p>
 * Compone la GUI della zona informativa delle linee.
 */
public class LineInfoView {

    // ROOT ------------------------------------------------------------------------------------------------------------
    private BorderPane lineInfoRoot;                            // Layout del pannello di informazione delle linee.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final LineInfoNode lineInfoNode;                    // Nodi per il pannello informativo delle linee.
    private final LineInfoLayout lineInfoLayout;                // Layout del pannello informativo delle linee.

    // CONTROLLER ------------------------------------------------------------------------------------------------------
    private final LineInfoController lineInfoController;        // Controller del pannello di informazione delle linee.

    // VIEW ------------------------------------------------------------------------------------------------------------
    private StopInfoView stopInfoView;                          // Riferimento al pannello informativo delle fermate.
    private MapsView mapsView;                                  // Riferimento alla mappa.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param lineInfoController controller del pannello informativo delle linee.
     */
    public LineInfoView(LineInfoController lineInfoController) {
        this.lineInfoController = lineInfoController;
        this.lineInfoNode = new LineInfoNode();
        this.lineInfoLayout = new LineInfoLayout();
    }

    // CREAZIONE DELLA UI ----------------------------------------------------------------------------------------------
    /**
     * Crea l'interfaccia della zona informativa delle linee, assembla tutti i layout e le view.
     */
    private void createView() {

        // CREAZIONE COMPONENTI PER LA BARRA DI RICERCA DELLE LINEE ----------------------------------------------------

        // Etichetta per il nome della linea.
        Label lineNameLabel = lineInfoNode.createLineNameLabel();
        lineNameLabel.setText("Prossime fermate della linea...");

        // Bottone per la selezione delle linee preferite.
        Button favoriteButton = lineInfoNode.createFavoriteButton();
        favoriteButton.setOnAction(_ -> {
            lineInfoController.toggleFavoriteForCurrentLine();                  // Notifica il controller che è stato cliccato.
            boolean isFav = lineInfoController.isCurrentLineFavorite();         // Verifica lo stato aggiornato.
            lineInfoNode.setFavoriteButtonState(favoriteButton, isFav);         // Aggiorna l’icona (colore).
        });

        // Bottone per il tipo di linea
        Button routeTypeButton = lineInfoNode.createRouteTypeButton();

        // Lista delle fermate della linea (ora Arrival invece di String).
        ListView<Arrival> stopsList = lineInfoNode.createStopsList();
        stopsList.setOnMouseClicked(_ -> {                                                                       // Quando si seleziona una riga della listView.
            Arrival selected = stopsList.getSelectionModel().getSelectedItem();                                             // Recupera l'Arrival selezionato.
            if (selected != null) {                                                                                         // Se l'Arrival è valido...
                Stop stop = lineInfoController.getStopById(selected.getStopId());                                           // ...Recupera la fermata corrispondente tramite ID.
                if (stop != null) {                                                                                         // Se la fermata è valida...
                    if (mapsView != null) {
                        mapsView.showStopOnMap(stop);                                                                       // ...Mostra la fermata corretta sulla mappa.
                    }
                    if (stopInfoView != null) {
                        stopInfoView.updateCurrentStop(stop);                                                               // ...Aggiorna lo StopInfoView con lo Stop corretto.
                    }
                }
            }
        });

        // Bottone per la selezione dell'orario di partenza.
        Button selectionTimeButton = lineInfoNode.createSelectionTimeButton();

        // Lista degli orari di partenza.
        ListView<String> timesList = lineInfoNode.createTimesList();
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

        // Mostra/nasconde popup cliccando sul bottone
        selectionTimeButton.setOnAction(_ -> {
            if (!timesPopup.isShowing()) {                                                                                  // Se il popup al momento del click del bottone non è visibile.
                Bounds bounds = selectionTimeButton.localToScreen(selectionTimeButton.getBoundsInLocal());                  // Calcola le coordinate del bottone sullo schermo
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
                lineInfoController.setSelectedTime(LocalTime.parse(selected));                                              // ...L'orario viene passato al controller sottoforma di oggetto LocalTime...
                timesPopup.hide();                                                                                          // ...Il popup viene nascosto...
                refreshCurrentLine();                                                                                       // ...Aggiorna il pannello in base al nuovo orario selezionato.
            }
        });

        // Bottone per la selezione dell'orario attuale.
        Button nowButton = lineInfoNode.createNowButton();
        nowButton.setOnAction(_ -> {
            LocalTime now = LocalTime.now();                                                                                // Salva l'orario corrente dal sistema...
            String selectedTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));                                         // ...Imposto l'orario nella stringa del formato HH:mm...
            selectionTimeButton.setText(selectedTime);                                                                      // ...Imposta l'orario sul selectionTimeButton...
            lineInfoController.setSelectedTime(now);                                                                        // ...Passa al controller l'orario selezionato...
            refreshCurrentLine();                                                                                           // ...Aggiorna il pannello in base al nuovo orario selezionato.
        });

        // ASSEMBLAGGIO DEL LAYOUT -------------------------------------------------------------------------------------

        HBox lineTitleBox = lineInfoLayout.createLineTitleBox(favoriteButton, lineNameLabel, routeTypeButton);
        HBox selectionTimeBox = lineInfoLayout.createSelectionTimeBox(selectionTimeButton, nowButton);
        VBox topBox = lineInfoLayout.createTopBox(lineTitleBox, selectionTimeBox);

        lineInfoRoot = lineInfoLayout.createLineInfoRoot();
        lineInfoRoot.setTop(topBox);
        lineInfoRoot.setCenter(stopsList);
    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Restituisce il pannello informativo delle linee.
     * Crea la view se non è stata ancora inizializzata.
     *
     * @return BorderPane contenente la view.
     */
    public BorderPane getView() {       // Chiamata in HomeLayout.
        if (lineInfoRoot == null) {     // Se il layout non è stato creato...
            createView();               // ...Lo crea.
        }
        return lineInfoRoot;
    }

    // AGGIORNA IL PANNELLO INFORMATIVO TRAMITE CASTING ----------------------------------------------------------------
    /**
     * Aggiorna i componenti grafici della view con una nuova linea e corsa.
     *
     * @param route linea selezionata.
     * @param trip  corsa selezionata.
     */
    private void updateLineInfoView(Route route, Trip trip) {
        VBox topBox = (VBox) lineInfoRoot.getTop();                                             // Recupera il layout della zona superiore del pannello.
        HBox titleHBox = (HBox) topBox.getChildren().get(0);                                    // Recupera il layout della zona del nome della linea.
        Button routeTypeButton = (Button) titleHBox.getChildren().get(2);                       // Recupera il bottone per il tipo di linea.
        Label lineNameLabel = (Label) titleHBox.getChildren().get(1);                           // Recupera l'etichetta del nome della linea.
        Button favoriteButton = (Button) titleHBox.getChildren().get(0);                        // Recupera il bottone dei preferiti.
        ListView<Arrival> stopsList = (ListView<Arrival>) lineInfoRoot.getCenter();             // Recupera la lista delle fermate della linea.

        lineInfoController.selectLine(route, trip, lineNameLabel, stopsList);                   // Chiama il controller per aggiornare la lista delle fermate della linea.
        boolean isFav = lineInfoController.isCurrentLineFavorite();                             // Chiama il controller per vedere se la linea è tra i preferiti (true) o no (false).
        lineInfoNode.setFavoriteButtonState(favoriteButton, isFav);                             // Cambia il colore del bottone dei preferiti in base a isFav.
        lineInfoNode.setRouteTypeButtonState(routeTypeButton, route.getRouteType());            // Cambia lo stato del bottone del tipo di linea (bus, metro, tram).
    }

    // AGGIORNAMENTO DEL PANNELLO QUANDO SI CAMBIA LA FERMATA ----------------------------------------------------------
    /**
     * Aggiorna il pannello quando si seleziona una nuova linea o corsa.
     *
     * @param route linea selezionata.
     * @param trip  corsa selezionata.
     */
    public void updateCurrentLine(Route route,
                                  Trip trip) {
        if (lineInfoRoot == null) createView();                                                 // Crea il pannello se non è stato ancora creato.
        updateLineInfoView(route, trip);                                                        // Aggiorna il pannello quando si seleziona un nuova linea.
    }

    // AGGIORNAMENTO DEL PANNELLO QUANDO SI CAMBIA L'ORARIO ------------------------------------------------------------
    /**
     * Aggiorna la view quando cambia l'orario selezionato.
     */
    private void refreshCurrentLine() {
        if (lineInfoRoot == null) return;
        Route currentRoute = lineInfoController.getCurrentRoute();                              // Riceve dal controller la linea corrente (sempre la stessa).
        Trip currentTrip = lineInfoController.getCurrentTrip();                                 // Riceve dal controller la corsa corrente.
        if (currentRoute != null && currentTrip != null) {
            updateLineInfoView(currentRoute, currentTrip);                                      // Aggiorna il pannello (cambia solo la lista delle fermate).
        }
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

    // SETTER DI COLLEGAMENTO CON STOPINFOVIEW -------------------------------------------------------------------------
    /**
     * Imposta il riferimento alla {@link StopInfoView}.
     *
     * @param stopInfoView {@link StopInfoView} da collegare.
     */
    public void setStopInfoView(StopInfoView stopInfoView) {
        this.stopInfoView = stopInfoView;
    }

    // RIMUOVE LA SCROLLBAR DALLA LISTVIEW -----------------------------------------------------------------------------
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
                        "-fx-background-color: TRANSPARENT;"+
                        "-fx-pref-width: 0;"+
                        "-fx-opacity: 0;"
                );
            }
        });
    }
}

