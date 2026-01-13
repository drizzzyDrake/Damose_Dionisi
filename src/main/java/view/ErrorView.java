package view;

// Layout e Nodi.
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;
import view.layouts.ErrorLayout;
import view.layouts.TitleBarLayout;
import view.nodes.ErrorNode;
import view.nodes.TitleBarNode;

// Classi javaFX necessarie per mostrare la GUI.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.Modality;

/**
 * Classe view per la finestra d'errore.
 * <p>
 * Compone la GUI della finestra d'errore.
 */
public class ErrorView {

    // STAGE -----------------------------------------------------------------------------------------------------------
    private final Stage errorStage;                 // Finestra principale (passata da app.Main).

    // SCENA -----------------------------------------------------------------------------------------------------------
    private final Scene errorScene;                 // Scena della finestra d'errore.

    // ROOT ------------------------------------------------------------------------------------------------------------
    private final BorderPane errorRoot;             // Layout della finestra d'errore.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final TitleBarNode titleBarNode;         // Nodi della titlebar.
    private final ErrorNode errorNode;               // Nodi della finestra d'errore.
    private final TitleBarLayout titleBarLayout;     // Layout della titlebar
    private final ErrorLayout errorLayout;           // Layout della finestra d'errore.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private final Label errorLabel;                  // Etichetta del messaggio d'errore (utilizzata da updateMessage()).

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param ownerStage finestra principale.
     */
    public ErrorView(Stage ownerStage) {
        this.titleBarNode = new TitleBarNode();
        this.errorNode = new ErrorNode();
        this.titleBarLayout = new TitleBarLayout();
        this.errorLayout = new ErrorLayout();

        // CREAZIONE DELLA FINESTRA ------------------------------------------------------------------------------------

        errorStage = new Stage(StageStyle.TRANSPARENT);
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.initOwner(ownerStage);

        // CREAZIONE COMPONENTI PER LA TITLEBAR ------------------------------------------------------------------------

        // Bottone per la riduzione a icona.
        Button minimizeButton = titleBarNode.createMinimizeButton();
        minimizeButton.setOnAction(_ -> errorStage.setIconified(true));

        // Bottone per il full screen.
        Button maximizeButton = titleBarNode.createMaximizeButton();
        maximizeButton.setOnAction(_ -> {
            if (errorStage.isFullScreen()) {                // Se la finestra è in full screen...
                errorStage.setFullScreen(false);            // ...Esce dal full screen...
                errorStage.setResizable(true);              // ...Ora può essere ridimensionata.
            } else {                                        // Se la finestra non è in full screen...
                errorStage.setFullScreen(true);             // ...Entra in full screen...
                errorStage.setResizable(false);             // ...Disattiva ridimensionamento.
            }
        });
        maximizeButton.setDisable(true);                    // Disattivo la funzionalità per questa finestra.
        maximizeButton.setOpacity(0.5);

        // Bottone per la chiusura della finestra.
        Button closeButton = titleBarNode.createCloseButton();
        closeButton.setOnAction(_ -> errorStage.close());

        // CREAZIONE COMPONENTI PER LA FINESTRA D'ERRORE ---------------------------------------------------------------

        // Icona del triangolo d'allerta.
        Button warningButton = errorNode.createWarningButton();

        // Etichetta del messaggio d'errore.
        errorLabel = errorNode.createErrorLabel();

        // ASSEMBLAGGIO DEL LAYOUT -------------------------------------------------------------------------------------

        HBox titleBar = titleBarLayout.createTitleBarBox(minimizeButton, maximizeButton, closeButton);
        HBox errorBox = errorLayout.createErrorBox(warningButton, errorLabel);

        errorRoot = errorLayout.createErrorRoot(titleBar, errorBox);

        // SETTAGGIO DELLA SCENA ---------------------------------------------------------------------------------------

        errorScene = new Scene(errorRoot, 500, 200);
        errorScene.setFill(null);

        // SETTAGGIO DELLA FINESTRA ------------------------------------------------------------------------------------

        errorStage.setScene(errorScene);
        errorStage.setTitle("Error");
        errorStage.setResizable(false);
        errorStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/damose-error.png")));
    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Mostra la finestra d'errore in modalità thread-safe.
     */
    public void show() {
        Platform.runLater(errorStage::show);
    }

    // AGGIORNA IL MESSAGGIO NELLA FINESTRA D'ERRORE -------------------------------------------------------------------
    /**
     * Aggiorna il messaggio visualizzato nella finestra d'errore.
     *
     * @param errorMessage Testo del messaggio d'errore da visualizzare
     */
    public void updateMessage(String errorMessage) {
        Platform.runLater(() -> errorLabel.setText(errorMessage));
    }
}


