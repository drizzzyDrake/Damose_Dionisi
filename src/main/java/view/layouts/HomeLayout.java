package view.layouts;

// View.
import view.*;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.*;

/**
 * Classe layout per la home.
 * <p>
 * Gestisce la disposizione del layout della home, composto da più livelli:
 * background, mappa, griglia strumenti e overlay (title bar).
 * Include metodi per creare VBox e GridPane nelle diverse posizioni della griglia.
 */
public class HomeLayout {

    /**
     * Crea la root della home, utilizzando uno StackPane con 4 livelli.
     *
     * @param backgroundRegion Region per il background
     * @param mapsView         MapsView contenente la mappa
     * @param underlayPane     GridPane con la griglia degli strumenti
     * @param overlayPane      BorderPane con l'overlay (title bar)
     * @return                 StackPane root della home
     */
    public StackPane createHomeRoot(Region backgroundRegion,
                                    MapsView mapsView,
                                    GridPane underlayPane,
                                    BorderPane overlayPane) {
        StackPane homeRoot = new StackPane();
        homeRoot.getChildren().add(backgroundRegion);                       // - Livello 0: Background.
        homeRoot.getChildren().add(mapsView.getView());                     // - Livello 1: Mappa su tutto lo schermo.
        homeRoot.getChildren().add(underlayPane);                           // - Livello 2: Griglia degli strumenti.
        homeRoot.getChildren().add(overlayPane);                            // - Livello 3: TitleBar.
        homeRoot.setStyle(
                "-fx-background-color: TRANSPARENT;"+
                        "-fx-font-size: 15;"
        );
        StackPane.setAlignment(underlayPane, Pos.TOP_LEFT);                 // Imposta la griglia a partire dal punto di coordinate (0,0) dello StackPane.
        StackPane.setAlignment(overlayPane, Pos.TOP_LEFT);                  // Imposta la border a partire dal punto di coordinate (0,0) dello StackPane.

        return homeRoot;
    }

    /**
     * Crea il layout per il background [livello 0].
     *
     * @return Region di sfondo
     */
    public Region createBackgroundRegion() {
        Region backgroundRegion = new Region();
        backgroundRegion.setStyle(
                "-fx-background-color: #323232;"+
                        "-fx-background-radius: 20;"+
                        "-fx-border-radius: 20;"
        );
        return backgroundRegion;
    }

    /**
     * Crea il layout per la griglia degli strumenti [livello 2].
     *
     * @param oneBox   VBox posizione (0,0)
     * @param twoBox   VBox posizione (1,0)
     * @param threeBox VBox posizione (2,0)
     * @param fourBox  VBox posizione (0,1)
     * @param fiveBox  VBox posizione (1,1)
     * @param sixBox   VBox posizione (2,1)
     * @param sevenBox VBox posizione (0,2)
     * @param eightBox VBox posizione (1,2)
     * @param nineBox  VBox posizione (2,2)
     * @return         GridPane con la griglia 3x3
     */
    public GridPane createUnderlayPane(VBox oneBox,
                                       VBox twoBox,
                                       VBox threeBox,
                                       VBox fourBox,
                                       VBox fiveBox,
                                       VBox sixBox,
                                       VBox sevenBox,
                                       VBox eightBox,
                                       VBox nineBox) {
        GridPane underlayPane = new GridPane();
        underlayPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        underlayPane.setPickOnBounds(false);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            underlayPane.getColumnConstraints().add(col);
        }

        for (int i = 0; i < 3; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(33.33);
            underlayPane.getRowConstraints().add(row);
        }

        underlayPane.add(oneBox, 0, 0);
        underlayPane.add(twoBox, 1, 0);
        underlayPane.add(threeBox, 2, 0);
        underlayPane.add(fourBox, 0, 1);
        underlayPane.add(fiveBox, 1, 1);
        underlayPane.add(sixBox, 2, 1);
        underlayPane.add(sevenBox, 0, 2);
        underlayPane.add(eightBox, 1, 2);
        underlayPane.add(nineBox, 2, 2);

        return underlayPane;
    }

    /**
     * Crea il layout per la border [livello 3].
     *
     * @param titleBarBox VBox contenente la title bar
     * @return            BorderPane overlay
     */
    public BorderPane createOverlayPane(VBox titleBarBox) {
        BorderPane overlayPane = new BorderPane();
        overlayPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlayPane.setPickOnBounds(false);
        overlayPane.setTop(titleBarBox);
        return overlayPane;
    }

    /**
     * Crea la VBox per la titleBar.
     *
     * @param titleBar HBox della title bar
     * @return         VBox contenente la title bar con margini e stile
     */
    public VBox createTitleBarBox(HBox titleBar) {
        VBox titleBarBox = new VBox(20);
        titleBarBox.setPickOnBounds(false);
        titleBarBox.setStyle(
                "-fx-background-color: rgba(0,0,0,0.8);"+
                        "-fx-border-radius: 30;"+
                        "-fx-background-radius: 30;"
        );

        titleBarBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        titleBarBox.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        titleBarBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        BorderPane.setAlignment(titleBarBox, Pos.TOP_RIGHT);
        BorderPane.setMargin(titleBarBox, new Insets(10));

        titleBarBox.getChildren().add(titleBar);
        return titleBarBox;
    }

    /**
     * Crea la VBox di coordinate (0,0) con stopView e lineView.
     *
     * @param findStopView vista per la ricerca fermate
     * @param findLineView vista per la ricerca linee
     * @return             VBox contenente le viste di ricerca
     */
    public VBox createOneBox(FindStopView findStopView,
                             FindLineView findLineView) {
        VBox oneBox = new VBox(20);
        oneBox.setPickOnBounds(false);
        oneBox.setSpacing(-5);

        GridPane.setHalignment(oneBox, HPos.LEFT);
        GridPane.setValignment(oneBox, VPos.TOP);
        GridPane.setMargin(oneBox, new Insets(10));

        oneBox.getChildren().addAll(findStopView.getView(), findLineView.getView());
        return oneBox;
    }

    /**
     * Crea la VBox di coordinate (1,0).
     *
     * @return VBox vuota
     */
    public VBox createTwoBox() {
        VBox twoBox = new VBox(20);
        twoBox.setPickOnBounds(false);
        return twoBox;
    }

    /**
     * Crea la VBox di coordinate (2,0).
     *
     * @return VBox vuota
     */
    public VBox createThreeBox() {
        VBox threeBox = new VBox(20);
        threeBox.setPickOnBounds(false);
        return threeBox;
    }

    /**
     * Crea la VBox di coordinate (0,1).
     *
     * @return VBox vuota
     */
    public VBox createFourBox() {
        VBox fourBox = new VBox(20);
        fourBox.setPickOnBounds(false);
        return fourBox;
    }

    /**
     * Crea la VBox di coordinate (1,1).
     *
     * @return VBox vuota
     */
    public VBox createFiveBox() {
        VBox fiveBox = new VBox(20);
        fiveBox.setPickOnBounds(false);
        return fiveBox;
    }

    /**
     * Crea la VBox di coordinate (2,1) con ToolBarView.
     *
     * @param toolBarView vista della toolbar
     * @return            VBox contenente la toolbar
     */
    public VBox createSixBox(ToolBarView toolBarView) {
        VBox sixBox = new VBox(20);
        sixBox.setPickOnBounds(false);

        GridPane.setHalignment(sixBox, HPos.RIGHT);
        GridPane.setValignment(sixBox, VPos.CENTER);
        GridPane.setMargin(sixBox, new Insets(10));

        sixBox.getChildren().add(toolBarView.getView());
        return sixBox;
    }

    /**
     * Crea la VBox di coordinate (0,2) con StopInfoView.
     *
     * @param stopInfoView vista informazioni fermata
     * @return             VBox contenente le informazioni della fermata
     */
    public VBox createSevenBox(StopInfoView stopInfoView) {
        VBox sevenBox = new VBox(20);
        sevenBox.setPickOnBounds(false);

        GridPane.setHalignment(sevenBox, HPos.LEFT);
        GridPane.setValignment(sevenBox, VPos.BOTTOM);
        GridPane.setMargin(sevenBox, new Insets(10));

        sevenBox.getChildren().add(stopInfoView.getView());
        return sevenBox;
    }

    /**
     * Crea la VBox di coordinate (1,2) con LineInfoView.
     *
     * @param lineInfoView vista informazioni linea
     * @return             VBox contenente le informazioni della linea
     */
    public VBox createEightBox(LineInfoView lineInfoView) {
        VBox eightBox = new VBox(20);
        eightBox.setPickOnBounds(false);

        GridPane.setHalignment(eightBox, HPos.CENTER);
        GridPane.setValignment(eightBox, VPos.BOTTOM);
        GridPane.setMargin(eightBox, new Insets(10));

        eightBox.getChildren().add(lineInfoView.getView());
        return eightBox;
    }

    /**
     * Crea la VBox di coordinate (2,2).
     *
     * @return VBox vuota
     */
    public VBox createNineBox() {
        VBox nineBox = new VBox(20);
        nineBox.setPickOnBounds(false);
        return nineBox;
    }
}

