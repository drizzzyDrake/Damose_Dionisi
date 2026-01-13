package view;

// Model.
import model.Route;
import model.Trip;

// Controller.
import controller.FindLineController;

// Layout e nodi.
import view.layouts.FindLineLayout;
import view.nodes.FindLineNode;

// Classi javaFX necessarie per mostrare la GUI.
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.application.Platform;
import javafx.geometry.Bounds;

// Altre classi
import java.util.List;

/**
 * Classe view per la zona di ricerca delle linee.
 * <p>
 * Compone la GUI della zona di ricerca delle linee.
 */
public class FindLineView {

    // ROOT ------------------------------------------------------------------------------------------------------------
    private VBox searchLineRoot;                            // Layout della zona di ricerca delle linee.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final FindLineNode findLineNode;                // Nodi della barra di ricerca.
    private final FindLineLayout findLineLayout;            // Layout della barra di ricerca.

    // CONTROLLER ------------------------------------------------------------------------------------------------------
    private final FindLineController findLineController;    // Controller della barra di ricerca delle linee.

    // VIEW ------------------------------------------------------------------------------------------------------------
    private LineInfoView lineInfoView;                      // Riferimento al pannello informativo.
    private MapsView mapsView;                              // Riferimento alla mappa.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param findLineController Controller della ricerca delle linee.
     */
    public FindLineView(FindLineController findLineController) {
        this.findLineController = findLineController;

        this.findLineNode = new FindLineNode();
        this.findLineLayout = new FindLineLayout();
    }

    // CREAZIONE DELLA UI ----------------------------------------------------------------------------------------------
    /**
     * Crea l'interfaccia della zona di ricerca delle linee assembla tutti i layout e le view.
     */
    private void createView() {

        // CREAZIONE COMPONENTI PER LA BARRA DI RICERCA DELLE LINEE ----------------------------------------------------

        // Campo per la ricerca della linea.
        TextField searchLineField = findLineNode.createSearchLineField();

        // Bottone per la ricerca.
        Button findLineButton = findLineNode.createFindLineButton();

        // Tendina dinamica dei suggerimenti.
        ListView<String> suggestionList = findLineNode.createSuggestionList();

        // Creazione del popup che conterrà la tendina dinamica dei suggerimenti.
        Popup suggestionPopup = new Popup();
        suggestionPopup.setAutoHide(true);                                                                                  // Si chiude automaticamente quando si clicca altrove.
        suggestionPopup.setAutoFix(true);                                                                                   // Si riposiziona automaticamente se va fuori schermo.
        suggestionPopup.getContent().add(suggestionList);                                                                   // Aggiunge la ListView al contenuto del popup.

        // Ogni volta che il testo cambia, viene eseguito il blocco di codice.
        searchLineField.textProperty().addListener((obs, oldQuery, newQuery) -> {
            if (newQuery.isEmpty()) {                                                                                       // Se il campo è vuoto...
                suggestionPopup.hide();                                                                                     // ...Nasconde il popup...
                suggestionList.getItems().clear();                                                                          // ...E cancella tutti gli elementi suggeriti.
            } else {                                                                                                        // Se il campo non è vuoto...
                List<String> items = findLineController.searchLines(newQuery);                                              // ...Chiama il metodo searchLines(newQuery) del controller, che restituisce una lista di stringhe "NOME LINEA - DIREZIONE [routeID|dirID]"...
                suggestionList.getItems().setAll(items);                                                                    // ...Aggiorna la lista dei suggerimenti con i nomi trovati...
                if (items.isEmpty()) {                                                                                      // ...Se non ci sono risultati...
                    suggestionPopup.hide();                                                                                 // ...Nasconde il popup...
                } else {                                                                                                    // ...Se ci sono risultati...
                    // ...Aggiorna dimensioni dinamiche.
                    int maxVisibleItems = 7;                                                                                // Massimo di righe visibili.
                    int itemCount = Math.min(items.size(), maxVisibleItems);                                                // Righe attualmente visibili.
                    double itemHeight = 25;                                                                                 // Altezza di ogni riga.
                    double totalHeight = itemCount * itemHeight;                                                            // Altezza totale della tendina (numero righe x altezza righe).
                    suggestionList.setPrefHeight(totalHeight);                                                              // Imposta l'altezza.
                    suggestionList.setPrefWidth(searchLineField.getWidth());                                                // Imposta la larghezza (come la barra di ricerca)

                    if (!suggestionPopup.isShowing()) {                                                                     // Se il popup al momento del click del bottone non è visibile...
                        Bounds bounds = searchLineField.localToScreen(searchLineField.getBoundsInLocal());                  // ...Calcola le coordinate del campo sullo schermo.
                        double popupX = bounds.getMinX();                                                                   // Allineato orizzontalmente al campo.
                        double popupY = bounds.getMaxY() + 5;                                                               // Sotto al campo distanziato di 5 pixel.
                        suggestionPopup.show(searchLineField, popupX, popupY);                                              // Mostra il popup alle coordinate scelte.
                    }

                    // Toglie la barra di scorrimento verticale.
                    removeScrollBar(suggestionList);
                }
            }
        });

        // Quando si clicca su una linea, viene eseguito il blocco di codice.
        suggestionList.setOnMouseClicked(_ -> {
            String selectedItem = suggestionList.getSelectionModel().getSelectedItem();                                     // Ottiene l'elemento selezionato, del tipo: "NOME LINEA - DIREZIONE [routeID|dirID]".
            if (selectedItem != null) {                                                                                     // Se l'elemento è valido...
                String routeId = selectedItem
                        .substring(selectedItem.lastIndexOf('[') + 1, selectedItem.lastIndexOf('|'));               // ...Estrae lo route_id...
                String dirId = selectedItem
                        .substring(selectedItem.lastIndexOf('|') + 1, selectedItem.lastIndexOf(']'));               // ...Estrae lo dir_id...
                Route selectedRoute = findLineController.getRouteById(routeId);                                             // ...Trova la linea corrispondente usando lo route_id...
                Trip firstTrip = findLineController.getFirstTrip(routeId, Integer.parseInt(dirId));                         // ...Trova la prima corsa della linea...
                if (firstTrip != null && selectedRoute != null) {                                                           // ...Se è stata trovata una corsa valida...
                    searchLineField.setText(
                            selectedRoute.getRouteShortName() + " → " + firstTrip.getTripHeadsign());                       // ...Mostra il nome della linea con il capolinea.
                    if (mapsView != null) {
                        mapsView.showLineOnMap(firstTrip);                                                                  // Mostra la linea sulla mappa.
                    }
                    if (lineInfoView != null) {
                        lineInfoView.updateCurrentLine(selectedRoute, firstTrip);                                           // Aggiorna il pannello informativo.
                    }
                }
                suggestionPopup.hide();                                                                                     // Dopo la selezione nasconde la ListView.
            }
        });

        // ASSEMBLAGGIO DEL LAYOUT -------------------------------------------------------------------------------------

        StackPane searchLinePane = findLineLayout.createSearchLinePane(searchLineField, findLineButton);
        searchLineRoot = findLineLayout.createSearchLineRoot(searchLinePane);
    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Restituisce il layout della barra di ricerca delle linee.
     * Crea la view se non è stata ancora inizializzata.
     *
     * @return VBox contenente la barra di ricerca.
     */
    public VBox getView() {                 // Chiamata in HomeLayout.
        if (searchLineRoot == null) {       // Se il layout non è stato creato...
            createView();                   // ...Lo crea.
        }
        return searchLineRoot;
    }

    // SETTER DI COLLEGAMENTO CON LINEINFOVIEW -------------------------------------------------------------------------
    /**
     * Imposta il riferimento alla {@link LineInfoView}.
     *
     * @param lineInfoView {@link LineInfoView} da collegare.
     */
    public void setLineInfoView(LineInfoView lineInfoView) {
        this.lineInfoView = lineInfoView;
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
