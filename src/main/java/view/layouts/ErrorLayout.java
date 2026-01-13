package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Classe layout per la finestra d'errore.
 * <p>
 * Gestisce la disposizione dei nodi della finestra d'errore
 * con title bar e zona centrale contenente il messaggio e l'icona di warning.
 */
public class ErrorLayout {

    /**
     * Crea la root della finestra d'errore, usando una BorderPane.
     *
     * @param titleBar HBox contenente la barra del titolo
     * @param errorBox HBox contenente il messaggio d'errore e eventuali bottoni
     * @return         BorderPane root della finestra d'errore
     */
    public BorderPane createErrorRoot(HBox titleBar, HBox errorBox){
        BorderPane errorRoot = new BorderPane();                 // Creo una borderpane per posizionare le due box.
        errorRoot.setTop(titleBar);                              // Zona della titlebar in alto.
        errorRoot.setCenter(errorBox);                           // Zona del messaggio d'errore al centro.
        errorRoot.setStyle(
                "-fx-background-radius: 20;"+
                "-fx-background-color: rgba(0,0,0,0.8);"
        );
        return errorRoot;
    }

    /**
     * Crea il layout per la zona del messaggio d'errore.
     *
     * @param warningButton Button per indicare il warning
     * @param errorLabel    Label contenente il messaggio d'errore
     * @return              HBox contenente il messaggio d'errore e il bottone
     */
    public HBox createErrorBox(Button warningButton,
                               Label errorLabel){
        HBox errorBox = new HBox();
        errorBox.setStyle(
                "-fx-padding: 10;"+
                "-fx-alignment: TOP_CENTER;"+
                "-fx-spacing: 20;"
        );
        // Compone il layout con i rispettivi bottoni.
        errorBox.getChildren().addAll(warningButton, errorLabel);
        return errorBox;
    }
}


