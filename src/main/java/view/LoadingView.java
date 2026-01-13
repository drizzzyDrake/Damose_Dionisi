package view;

// Classi javaFX necessarie per mostrare la GUI.
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import view.layouts.LoadingLayout;
import view.nodes.LoadingNode;

/**
 * Classe view per la finestra di caricamento.
 * <p>
 * Compone la GUI della finestra di caricamento
 */
public class LoadingView {

    // STAGE -----------------------------------------------------------------------------------------------------------
    private final Stage loadingStage;           // Stage della finestra del caricamento.

    // SCENA -----------------------------------------------------------------------------------------------------------
    private final Scene loadingScene;           // Scena della finestra del caricamento.

    // ROOT ------------------------------------------------------------------------------------------------------------
    private final VBox loadingRoot;             // Layout della finestra del caricamento.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final LoadingNode loadingNode;      // Nodi della finestra di caricamento.
    private final LoadingLayout loadingLayout;  // Layout della finestra di caricamento.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private final Label loadingLabel;           // Etichetta del messaggio di caricamento (utilizzata da updateMessage()).

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param ownerStage finestra principale.
     */
    public LoadingView(Stage ownerStage) {
        this.loadingNode = new LoadingNode();
        this.loadingLayout = new LoadingLayout();

        // CREAZIONE DELLA FINESTRA ------------------------------------------------------------------------------------

        loadingStage = new Stage(StageStyle.TRANSPARENT);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.initOwner(ownerStage);
        loadingStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/damose-loading.png")));

        // CREAZIONE COMPONENTI PER LA FINESTRA DI CARICAMENTO ---------------------------------------------------------

        // Rotellina del caricamento.
        ProgressIndicator loadingProgressIndicator = loadingNode.createLoadingProgressIndicator();

        // Etichetta del messaggio di caricamento.
        loadingLabel = loadingNode.createLoadingLabel();

        // ASSEMBLAGGIO DEL LAYOUT -------------------------------------------------------------------------------------

        loadingRoot = loadingLayout.createLoadingRoot(loadingProgressIndicator, loadingLabel);

        // SETTAGGIO DELLA SCENA ---------------------------------------------------------------------------------------

        loadingScene = new Scene(loadingRoot, 350, 250);
        loadingScene.setFill(null);

        // SETTAGGIO DELLA FINESTRA ------------------------------------------------------------------------------------

        loadingStage.setScene(loadingScene);
        loadingStage.setTitle("Loading");
        loadingStage.setResizable(false);
    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Mostra la finestra di caricamento in modalità thread-safe.
     */
    public void show() { Platform.runLater(loadingStage::show); }

    // AGGIORNA IL MESSAGGIO NELLA FINESTRA DI CARICAMENTO -------------------------------------------------------------
    /**
     * Aggiorna il messaggio visualizzato nella finestra di caricamento.
     *
     * @param message testo da visualizzare nella finestra.
     */
    public void updateMessage(String message) {
        Platform.runLater(() -> loadingLabel.setText(message));
    }

    // CHIUDE LA FINESTRA DI CARICAMENTO -------------------------------------------------------------------------------
    /**
     * Chiude la finestra di caricamento in modalità thread-safe.
     */
    public void close() {
        Platform.runLater(loadingStage::close);
    }
}

