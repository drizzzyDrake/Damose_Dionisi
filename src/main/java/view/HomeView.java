package view;

// Controller.
import controller.*;

// Layout e nodi.
import view.layouts.HomeLayout;
import view.layouts.TitleBarLayout;
import view.nodes.TitleBarNode;
import view.nodes.WelcomeNode;

// Classi JavaFX necessarie per mostrare la GUI.
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.stage.Popup;

/**
 * Classe view per la home.
 * <p>
 * Compone la GUI principale del programma.
 */
public class HomeView {

    // STAGE -----------------------------------------------------------------------------------------------------------
    private final Stage homeStage;                          // Finestra principale (passata da app.Main).

    // SCENA -----------------------------------------------------------------------------------------------------------
    private Scene homeScene;                                // Scena della home.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final WelcomeNode welcomeNode;                  // Nodi del messaggi di benvenuto.
    private final TitleBarNode titleBarNode;                // Nodi della titlebar.
    private final TitleBarLayout titleBarLayout;            // Layout della titlebar.
    private final HomeLayout  homeLayout;                   // Layout della home.

    // CONTROLLER ------------------------------------------------------------------------------------------------------
    private final MapsController mapsController;            // Controller della mappa.
    private final FindStopController findStopController;    // Controller della barra di ricerca delle fermate.
    private final StopInfoController stopInfoController;    // Controller del pannello di informazione delle fermate.
    private final FindLineController findLineController;    // Controller della barra di ricerca delle linee.
    private final LineInfoController lineInfoController;    // Controller del pannello di informazione delle linee.
    private final ToolBarController toolBarController;      // Controller della barra degli strumenti.

    // VIEW ------------------------------------------------------------------------------------------------------------
    private final FindStopView findStopView;                // View della barra di ricerca delle fermate.
    private final MapsView mapsView;                        // View della mappa.
    private final StopInfoView stopInfoView;                // View del pannello di informazione delle fermate.
    private final FindLineView findLineView;                // View della barra di ricerca delle linee.
    private final LineInfoView lineInfoView;                // View del pannello di informazione delle linee.
    private final ToolBarView toolBarView;                  // View della barra degli strumenti.

    // UTENTE CORRENTE -------------------------------------------------------------------------------------------------
    private final String currentUser;

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param homeStage          Stage principale.
     * @param currentUser        Utente corrente.
     * @param mapsController     Controller della mappa.
     * @param findStopController Controller della barra di ricerca delle fermate.
     * @param stopInfoController Controller del pannello informativo delle fermate.
     * @param findLineController Controller della barra di ricerca delle linee.
     * @param lineInfoController Controller del pannello informativo delle linee.
     * @param toolBarController  Controller della barra degli strumenti.
     */
    public HomeView(Stage homeStage,
                    String currentUser,
                    MapsController mapsController,
                    FindStopController findStopController,
                    StopInfoController stopInfoController,
                    FindLineController findLineController,
                    LineInfoController lineInfoController,
                    ToolBarController toolBarController) {

        this.homeStage = homeStage;

        this.currentUser = currentUser;

        this.mapsController = mapsController;
        this.findStopController = findStopController;
        this.stopInfoController = stopInfoController;
        this.findLineController = findLineController;
        this.lineInfoController = lineInfoController;
        this.toolBarController = toolBarController;

        this.findStopView = new FindStopView(findStopController);
        this.mapsView = new MapsView(mapsController);
        this.stopInfoView = new StopInfoView(stopInfoController);
        this.findLineView = new FindLineView(findLineController);
        this.lineInfoView = new LineInfoView(lineInfoController);
        this.toolBarView = new ToolBarView(toolBarController);

        this.welcomeNode = new WelcomeNode();
        this.titleBarNode = new TitleBarNode();
        this.titleBarLayout = new TitleBarLayout();
        this.homeLayout = new HomeLayout();
    }

    // CREAZIONE DELLA UI ----------------------------------------------------------------------------------------------
    /**
     * Crea l'interfaccia della home e assembla tutti i layout e le view.
     */
    public void createView() {

        // COLLEGAMENTI TRA VIEW ---------------------------------------------------------------------------------------
        findStopView.setStopInfoView(stopInfoView);
        findStopView.setMapsView(mapsView);
        findLineView.setLineInfoView(lineInfoView);
        findLineView.setMapsView(mapsView);
        toolBarView.setStopInfoView(stopInfoView);
        toolBarView.setLineInfoView(lineInfoView);
        toolBarView.setMapsView(mapsView);
        lineInfoView.setStopInfoView(stopInfoView);
        lineInfoView.setMapsView(mapsView);
        stopInfoView.setLineInfoView(lineInfoView);
        stopInfoView.setMapsView(mapsView);

        // CREAZIONE COMPONENTI PER LA TITLEBAR ------------------------------------------------------------------------
        Button minimizeButton = titleBarNode.createMinimizeButton();
        minimizeButton.setOnAction(_ -> homeStage.setIconified(true));

        Button maximizeButton = titleBarNode.createMaximizeButton();
        maximizeButton.setOnAction(_ -> {
            if (homeStage.isFullScreen()) {
                homeStage.setFullScreen(false);
                homeStage.setResizable(true);
            } else {
                homeStage.setFullScreen(true);
                homeStage.setResizable(false);
            }
        });
        homeStage.fullScreenProperty().addListener((obs, wasFull, isFull) ->
            mapsView.getOverlay().setMouseTransparent(isFull));

        Button closeButton = titleBarNode.createCloseButton();
        closeButton.setOnAction(_ ->  homeStage.close());

        // ASSEMBLAGGIO DEL LAYOUT -------------------------------------------------------------------------------------
        HBox titleBar = titleBarLayout.createTitleBarBox(minimizeButton, maximizeButton, closeButton);
        VBox titleBarBox = homeLayout.createTitleBarBox(titleBar);

        VBox oneBox = homeLayout.createOneBox(findStopView, findLineView);
        VBox twoBox = homeLayout.createTwoBox();
        VBox threeBox = homeLayout.createThreeBox();
        VBox fourBox = homeLayout.createFourBox();
        VBox fiveBox = homeLayout.createFiveBox();
        VBox sixBox = homeLayout.createSixBox(toolBarView);
        VBox sevenBox = homeLayout.createSevenBox(stopInfoView);
        VBox eightBox = homeLayout.createEightBox(lineInfoView);
        VBox nineBox = homeLayout.createNineBox();

        BorderPane overlayPane = homeLayout.createOverlayPane(titleBarBox);
        GridPane underlayPane = homeLayout.createUnderlayPane(oneBox, twoBox, threeBox, fourBox, fiveBox, sixBox, sevenBox, eightBox, nineBox);
        Region backgroundRegion = homeLayout.createBackgroundRegion();
        StackPane homeRoot = homeLayout.createHomeRoot(backgroundRegion, mapsView, underlayPane, overlayPane);

        backgroundRegion.prefWidthProperty().bind(homeRoot.widthProperty());
        backgroundRegion.prefHeightProperty().bind(homeRoot.heightProperty());

        // SETTAGGIO DELLA SCENA ---------------------------------------------------------------------------------------
        homeScene = new Scene(homeRoot, 1280, 720);
        homeScene.setFill(null);

        // SETTAGGIO DELLA FINESTRA ------------------------------------------------------------------------------------
        HomeView.makeResizableAndDraggable(homeStage, homeRoot);
        homeStage.setTitle("Home");
        homeStage.setScene(homeScene);
        homeStage.centerOnScreen();
        homeStage.show();
    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Mostra la finestra principale della home.
     * Se la scena non è ancora stata creata, la crea prima.
     */
    public void show() {
        if (homeScene == null) {
            createView();
        }
        homeStage.show();
        showWelcomePopup(currentUser);
    }

    // RESTITUISCE LA VIEW DELLA MAPPA ---------------------------------------------------------------------------------
    /**
     * Restituisce la view della mappa.
     *
     * @return MapsView della home.
     */
    public MapsView getMapsView() {
        return mapsView;        // Per refreshVehicle in Main.
    }

    // RESTITUISCE LA VIEW DELLA TOOLBAR -------------------------------------------------------------------------------
    /**
     * Restituisce la view della toolbar.
     *
     * @return ToolBarView della home.
     */
    public ToolBarView getToolBarView() {
        return toolBarView;     // Per logout in Main.
    }

    // MOSTRA IL MESAGGIO DI BENVENUTO INIZIALE ------------------------------------------------------------------------
    /**
     * Mostra il messaggio di benvenuto.
     *
     * @param currentUser utente corrente.
     */
    public void showWelcomePopup(String currentUser) {
        HBox welcomeBox = welcomeNode.createWelcomeBox(currentUser);
        Popup welcomePopup = new Popup();
        welcomePopup.setAutoHide(true);
        welcomePopup.getContent().add(welcomeBox);
        welcomePopup.show(homeStage);
    }

    // METODO PER RENDERE LA FINESTRA TRASCINABILE E RIDIMENSIONABILE --------------------------------------------------
    /**
     * Rende la finestra trascinabile e ridimensionabile manualmente.
     *
     * @param stage Stage principale.
     * @param root  Region principale su cui applicare i listener di drag/resize.
     */
    public static void makeResizableAndDraggable(Stage stage, Region root) {
        final int BORDER = 5;
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];
        final boolean[] resizing = {false};

        stage.setMinWidth(712);
        stage.setMinHeight(400);
        stage.setWidth(1280);
        stage.setHeight(720);

        root.setOnMouseMoved(e -> {
            if (stage.isFullScreen()) {
                root.setCursor(Cursor.DEFAULT);
                return;
            }

            double x = e.getX(), y = e.getY(), w = root.getWidth(), h = root.getHeight();
            Cursor c = Cursor.DEFAULT;

            if (x < (BORDER + 7) && y < (BORDER + 7)) c = Cursor.NW_RESIZE;
            else if (x > w - (BORDER + 7) && y < (BORDER + 7)) c = Cursor.NE_RESIZE;
            else if (x < (BORDER + 7) && y > h - (BORDER + 7)) c = Cursor.SW_RESIZE;
            else if (x > w - (BORDER + 7) && y > h - (BORDER + 7)) c = Cursor.SE_RESIZE;
            else if (x < BORDER) c = Cursor.W_RESIZE;
            else if (x > w - BORDER) c = Cursor.E_RESIZE;
            else if (y < BORDER) c = Cursor.N_RESIZE;
            else if (y > h - BORDER) c = Cursor.S_RESIZE;

            root.setCursor(c);
        });

        root.setOnMousePressed(e -> {
            if (stage.isFullScreen()) return;
            resizing[0] = (root.getCursor() != Cursor.DEFAULT);
            offsetX[0] = e.getSceneX();
            offsetY[0] = e.getSceneY();
        });

        root.setOnMouseDragged(e -> {
            if (stage.isFullScreen()) return;

            if (resizing[0]) {
                Cursor c = root.getCursor();
                double x = e.getScreenX(), y = e.getScreenY();
                double stageX = stage.getX(), stageY = stage.getY();
                double stageW = stage.getWidth(), stageH = stage.getHeight();

                if (c == Cursor.E_RESIZE || c == Cursor.SE_RESIZE || c == Cursor.NE_RESIZE)
                    stage.setWidth(x - stageX);
                if (c == Cursor.S_RESIZE || c == Cursor.SE_RESIZE || c == Cursor.SW_RESIZE)
                    stage.setHeight(y - stageY);
                if (c == Cursor.W_RESIZE || c == Cursor.SW_RESIZE || c == Cursor.NW_RESIZE) {
                    double newW = stageW - (x - stageX);
                    if (newW > stage.getMinWidth()) {
                        stage.setX(x);
                        stage.setWidth(newW);
                    }
                }
                if (c == Cursor.N_RESIZE || c == Cursor.NE_RESIZE || c == Cursor.NW_RESIZE) {
                    double newH = stageH - (y - stageY);
                    if (newH > stage.getMinHeight()) {
                        stage.setY(y);
                        stage.setHeight(newH);
                    }
                }
            } else {
                stage.setX(e.getScreenX() - offsetX[0]);
                stage.setY(e.getScreenY() - offsetY[0]);
            }
        });
    }
}

