package view;

// Model.
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import model.Stop;
import model.Route;
import model.Trip;

// Controller.
import controller.ToolBarController;

// Layout e nodi.
import view.layouts.ToolBarLayout;
import view.nodes.ToolBarNode;

// Classi javaFX necessarie per mostrare la GUI.
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.stage.Popup;
import javafx.application.Platform;
import javafx.geometry.Bounds;

/**
 * Classe view per la barra degli strumenti.
 * <p>
 * Compone la GUI della barra degli strumenti.
 */
public class ToolBarView {

    // ROOT ------------------------------------------------------------------------------------------------------------
    private VBox toolBarRoot;                                   // Layout della barra degli strumenti.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final ToolBarNode toolBarNode;                      // Nodi per la barra degli strumenti.
    private final ToolBarLayout toolBarLayout;                  // Layout della barra degli strumenti.

    // CONTROLLER ------------------------------------------------------------------------------------------------------
    private final ToolBarController toolBarController;          // Controller della barra degli strumenti.

    // VIEW ------------------------------------------------------------------------------------------------------------
    private LineInfoView lineInfoView;
    private StopInfoView stopInfoView;
    private MapsView mapsView;

    // RUNNABLE --------------------------------------------------------------------------------------------------------
    private Runnable onLogout;                                  // Runnable nel Main per funzione di logout.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param toolBarController controller della barra degli strumenti.
     */
    public ToolBarView(ToolBarController toolBarController) {
        this.toolBarController = toolBarController;
        this.toolBarNode = new ToolBarNode();
        this.toolBarLayout = new ToolBarLayout();
    }

    // CREAZIONE DELLA UI ----------------------------------------------------------------------------------------------
    /**
     * Crea l'interfaccia della barra degli strumenti, assembla tutti i layout e le view.
     */
    private void createView() {

        // Bottone per lo stato di connettività.
        Button connectivityButton = toolBarNode.createConnectivityButton();
        toolBarController.setConnectivityListener(isOnline ->
                Platform.runLater(() ->
                        toolBarNode.setConnectivityButtonState(connectivityButton, isOnline)                                // Aggiorna lo stato grafico del bottone in base alla connessione.
                )
        );

        // Bottone per la lista dei preferiti.
        Button favoritesListButton = toolBarNode.createFavoriteListButton();

        // Bottoni per la scelta tra fermate e linee.
        Button stopChoiseButton = toolBarNode.createChoiseButton("Fermate");
        Button lineChoiseButton = toolBarNode.createChoiseButton("Linee");

        // Liste dei preferiti (fermate e linee).
        ListView<String> stopFavoritesList = toolBarNode.createFavoriteList();
        ListView<String> lineFavoritesList = toolBarNode.createFavoriteList();

        // Popup per mostrare i preferiti.
        Popup favoritesPopup = new Popup();
        favoritesPopup.setAutoHide(true);

        // Box contenente i bottoni di scelta.
        HBox choiseBox = toolBarLayout.createChoiseBox(stopChoiseButton, lineChoiseButton);

        // Mostra/nasconde il popup dei preferiti cliccando sul bottone.
        favoritesListButton.setOnAction(_ -> {
            if (!favoritesPopup.isShowing()) {
                favoritesPopup.getContent().clear();
                favoritesPopup.getContent().addAll(choiseBox);
                Bounds bounds = favoritesListButton.localToScreen(favoritesListButton.getBoundsInLocal());                  // Calcola le coordinate del bottone.
                double popupX = bounds.getMinX() - 300 - 5;                                                                 // Posizione orizzontale del popup.
                double popupY = bounds.getMinY();                                                                           // Posizione verticale del popup.
                favoritesPopup.show(favoritesListButton, popupX, popupY);                                                   // Mostra il popup.
            } else {
                favoritesPopup.hide();                                                                                      // Nasconde il popup.
            }
        });

        // Azione per il bottone "Fermate".
        stopChoiseButton.setOnAction(_ -> {
            stopFavoritesList.getItems().setAll(toolBarController.updateStopFavoritesList());                               // Aggiorna la lista delle fermate preferite.
            favoritesPopup.getContent().clear();
            favoritesPopup.getContent().setAll(stopFavoritesList);
            PauseTransition delay = new PauseTransition(Duration.millis(0.1));                                           // Delay per rimuovere la scrollbar.
            delay.setOnFinished(_ -> removeScrollBar(stopFavoritesList));
            delay.play();
        });

        // Azione per il bottone "Linee".
        lineChoiseButton.setOnAction(_ -> {
            lineFavoritesList.getItems().setAll(toolBarController.updateLineFavoritesList());                               // Aggiorna la lista delle linee preferite.
            favoritesPopup.getContent().clear();
            favoritesPopup.getContent().setAll(lineFavoritesList);
            PauseTransition delay = new PauseTransition(Duration.millis(0.1));                                           // Delay per rimuovere la scrollbar.
            delay.setOnFinished(_ -> removeScrollBar(lineFavoritesList));
            delay.play();
        });

        // Selezione di una fermata dalla lista dei preferiti.
        stopFavoritesList.setOnMouseClicked(_ -> {
            String selectedItem = stopFavoritesList.getSelectionModel().getSelectedItem();                                  // Recupera la fermata selezionata.
            if (selectedItem != null) {
                favoritesPopup.hide();
                String stopId = selectedItem
                        .substring(selectedItem.lastIndexOf('[') + 1, selectedItem.lastIndexOf(']'));               // Estrae lo stopId dalla stringa.
                Stop selectedStop = toolBarController.getStopById(stopId);                                                  // Recupera la fermata dal controller.
                if (selectedStop != null) {
                    stopInfoView.updateCurrentStop(selectedStop);                                                           // Aggiorna la StopInfoView.
                    mapsView.showStopOnMap(selectedStop);                                                                   // Mostra la fermata sulla mappa.
                }
            }
        });

        // Selezione di una linea dalla lista dei preferiti.
        lineFavoritesList.setOnMouseClicked(_ -> {
            String selectedItem = lineFavoritesList.getSelectionModel().getSelectedItem();                                  // Recupera la linea selezionata.
            if (selectedItem != null) {
                favoritesPopup.hide();
                String routeId = selectedItem
                        .substring(selectedItem.lastIndexOf('[') + 1, selectedItem.lastIndexOf(']'));               // Estrae il routeId dalla stringa.
                String directionId = selectedItem
                        .substring(selectedItem.lastIndexOf('(') + 1, selectedItem.lastIndexOf(')'));               // Estrae il directionId dalla stringa.
                Route selectedRoute = toolBarController.getRouteById(routeId);                                              // Recupera la linea dal controller.
                Trip selectedTrip = toolBarController
                        .getTripsByRouteAndDirection(routeId, Integer.parseInt(directionId)).get(0);                        // Recupera la corsa corrispondente.
                if (selectedRoute != null && selectedTrip != null) {
                    lineInfoView.updateCurrentLine(selectedRoute, selectedTrip);                                            // Aggiorna la LineInfoView.
                    mapsView.showLineOnMap(selectedTrip);                                                                   // Mostra la linea sulla mappa.
                }
            }
        });

        // Bottone per la legenda.
        Button legendButton = toolBarNode.createLegendButton();
        VBox legendBox = toolBarNode.createLegendBox();

        // Popup per la legenda.
        Popup legendPopup = new Popup();
        legendPopup.setAutoHide(true);
        legendPopup.getContent().add(legendBox);

        // Mostra/nasconde la legenda cliccando sul bottone.
        legendButton.setOnAction(_ -> {
            if (!legendPopup.isShowing()) {
                Bounds bounds = legendButton.localToScreen(legendButton.getBoundsInLocal());                                // Calcola le coordinate del bottone.
                double popupX = bounds.getMinX() - 200 - 5;                                                                 // Posizione orizzontale del popup.
                double popupY = bounds.getMinY();                                                                           // Posizione verticale del popup.
                legendPopup.show(legendButton, popupX, popupY);                                                             // Mostra il popup.
            } else {
                legendPopup.hide();                                                                                         // Nasconde il popup.
            }
        });

        // Bottone per il logout.
        Button logoutButton = toolBarNode.createLogoutButton();
        logoutButton.setOnAction(_ -> {
            if (onLogout != null) {
                onLogout.run();   // Esegue la callback definita dal Main.
            }
        });

        // Assembla il layout principale della barra degli strumenti.
        toolBarRoot = toolBarLayout.createToolBarRoot(favoritesListButton, connectivityButton, legendButton, logoutButton);
    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Restituisce la zona informativa delle fermate.
     * Crea la view se non è stata ancora inizializzata.
     *
     * @return VBox contenente la view.
     */
    public VBox getView() {             // Chiamata in HomeLayout.
        if (toolBarRoot == null) {      // Se il layout non è stato creato...
            createView();               // ...Lo crea.
        }
        return toolBarRoot;
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

    // CALLBACK PER IL LOGOUT ---------------------------------------------------------------------------------------------
    /**
     * Imposta l'azione da eseguire al logout.
     *
     * @param onLogout callback da eseguire.
     */
    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
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


