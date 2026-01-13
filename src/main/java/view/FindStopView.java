package view;

// Model.
import model.Stop;

// Controller.
import controller.FindStopController;

// Layout e nodi.
import view.layouts.FindStopLayout;
import view.nodes.FindStopNode;

// Classi javaFX necessarie per mostrare la GUI.
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.stage.Popup;

// Altre classi
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe view per la zona di ricerca delle fermate.
 * <p>
 * Compone la GUI della zona di ricerca delle fermate.
 */
public class FindStopView {

    // ROOT ------------------------------------------------------------------------------------------------------------
    private VBox searchStopRoot;                            // Layout della zona di ricerca delle fermate.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final FindStopNode findStopNode;                // Nodi della barra di ricerca.
    private final FindStopLayout findStopLayout;            // Layout della barra di ricerca.

    // CONTROLLER ------------------------------------------------------------------------------------------------------
    private final FindStopController findStopController;    // Controller della barra di ricerca delle fermate.

    // VIEW ------------------------------------------------------------------------------------------------------------
    private StopInfoView stopInfoView;                      // Riferimento al pannello informativo.
    private MapsView mapsView;                              // Riferimento alla mappa.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param findStopController Controller della ricerca delle fermate.
     */
    public FindStopView(FindStopController findStopController) {
        this.findStopController = findStopController;

        this.findStopNode = new FindStopNode();
        this.findStopLayout = new FindStopLayout();
    }

    // CREAZIONE DELLA UI ----------------------------------------------------------------------------------------------
    /**
     * Crea l'interfaccia della zona di ricerca delle fermate assembla tutti i layout e le view.
     */
    private void createView() {

        // CREAZIONE COMPONENTI PER LA BARRA DI RICERCA DELLE FERMATE --------------------------------------------------

        // Campo per la ricerca della fermata.
        TextField searchStopField = findStopNode.createSearchStopField();

        // Bottone per la ricerca.
        Button findStopButton = findStopNode.createFindStopButton();

        // Tendina dinamica dei suggerimenti.
        ListView<String> suggestionList = findStopNode.createSuggestionList();

        // Creazione del popup che conterrà la tendina dinamica dei suggerimenti.
        Popup suggestionPopup = new Popup();
        suggestionPopup.setAutoHide(true);                                                                                  // Si chiude automaticamente quando si clicca altrove.
        suggestionPopup.setAutoFix(true);                                                                                   // Si riposiziona automaticamente se va fuori schermo.
        suggestionPopup.getContent().add(suggestionList);                                                                   // Aggiunge la ListView al contenuto del popup.

        // Ogni volta che il testo cambia, viene eseguito il blocco di codice.
        searchStopField.textProperty().addListener((obs, oldQuery, newQuery) -> {
            if (newQuery.isEmpty()) {                                                                                       // Se il campo è vuoto...
                suggestionPopup.hide();                                                                                     // ...Nasconde il popup...
                suggestionList.getItems().clear();                                                                          // ...E cancella tutti gli elementi suggeriti.
            } else {                                                                                                        // Se il campo non è vuoto...
                List<String> items = findStopController.searchStops(newQuery);                                              // ...Chiama il metodo searchStops(newQuery) del controller, che restituisce la lista dei suggerimenti...
                suggestionList.getItems().setAll(items);                                                                    // Aggiorna la lista dei suggerimenti con i nomi trovati...
                if (items.isEmpty()) {                                                                                      // ...Se non ci sono risultati...
                    suggestionPopup.hide();                                                                                 // ...Nasconde il popup...
                } else {                                                                                                    // ...Se ci sono risultati...
                    // ...Aggiorna dimensioni dinamiche.
                    int maxVisibleItems = 7;                                                                                // Massimo di righe visibili.
                    int itemCount = Math.min(items.size(), maxVisibleItems);                                                // Righe attualmente visibili.
                    double itemHeight = 25;                                                                                 // Altezza di ogni riga.
                    double totalHeight = itemCount * itemHeight;                                                            // Altezza totale della tendina (numero righe x altezza righe).
                    suggestionList.setPrefHeight(totalHeight);                                                              // Imposta l'altezza.
                    suggestionList.setPrefWidth(searchStopField.getWidth());                                                // Imposta la larghezza (come la barra di ricerca)

                    if (!suggestionPopup.isShowing()) {                                                                     // Se il popup non è visibile...
                        Bounds bounds = searchStopField.localToScreen(searchStopField.getBoundsInLocal());                  // ...Calcola le coordinate del campo sullo schermo.
                        double popupX = bounds.getMinX();                                                                   // Allineato orizzontalmente al campo.
                        double popupY = bounds.getMaxY() + 5;                                                               // Sotto al campo distanziato di 5 pixel.
                        suggestionPopup.show(searchStopField, popupX, popupY);                                              // Mostra il popup alle coordinate scelte.
                    }

                    // Toglie la barra di scorrimento verticale.
                    removeScrollBar(suggestionList);
                }
            }
        });

        // Quando si clicca su una fermata, viene eseguito il blocco di codice.
        suggestionList.setOnMouseClicked(_ -> {
            String selectedItem = suggestionList.getSelectionModel().getSelectedItem();                                     // Ottiene l'elemento selezionato, del tipo: "NOME FERMATA [ID]".
            if (selectedItem != null) {                                                                                     // Se l'elemento è valido...
                String stopId = selectedItem
                        .substring(selectedItem.lastIndexOf('[') + 1, selectedItem.lastIndexOf(']'));               // ...Estrae lo stop_id dal formato "NOME FERMATA [ID]" e...
                Stop selectedStop = findStopController.getStopById(stopId);                                                 // ...Trova la fermata corrispondente usando lo stop_id.
                if (selectedStop != null) {                                                                                 // Se è stata trovata una fermata valida...
                    searchStopField.setText(selectedStop.getStopName());                                                    // Mostra il nome della fermata nella barra di ricerca.
                    if (mapsView != null) {
                        mapsView.showStopOnMap(selectedStop);                                                               // Mostra la fermata sulla mappa.
                    }
                    if (stopInfoView != null) {
                        stopInfoView.updateCurrentStop(selectedStop);                                                       // Aggiorna il pannello informativo.
                    }
                }
                suggestionPopup.hide();                                                                                     // Dopo la selezione nasconde la ListView.
            }
        });

        // ASSEMBLAGGIO DEL LAYOUT -------------------------------------------------------------------------------------

        StackPane searchStopPane = findStopLayout.createSearchStopPane(searchStopField, findStopButton);
        searchStopRoot = findStopLayout.createSearchStopRoot(searchStopPane);
    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Restituisce il layout della barra di ricerca delle fermate.
     * Crea la view se non è stata ancora inizializzata.
     *
     * @return VBox contenente la barra di ricerca.
     */
    public VBox getView() {
        if (searchStopRoot == null) {       // Se il layout non è stato creato...
            createView();                   // ...Lo crea.
        }
        return searchStopRoot;
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

    // SETTER DI COLLEGAMENTO CON MAPSVIEW -----------------------------------------------------------------------------
    /**
     * Imposta il riferimento alla {@link MapsView}.
     *
     * @param mapsView {@link MapsView} da collegare.
     */
    public void setMapsView(MapsView mapsView) {
        this.mapsView = mapsView;
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
                        "-fx-background-color: TRANSPARENT;" +
                        "-fx-pref-width: 0;" +
                        "-fx-opacity: 0;"
                );
            }
        });
    }
}


