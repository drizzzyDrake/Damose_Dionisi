package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.paint.Color;

/**
 * Classe node per la finestra d'errore.
 * <p>
 * Fornisce i componenti grafici della finestra d'errore.
 * </p>
 */
public class ErrorNode {

    /**
     * Crea l'etichetta per i messaggi d'errore.
     *
     * @return Label formattata per mostrare i messaggi d'errore
     */
    public Label createErrorLabel(){
        Label errorLabel = new Label();
        errorLabel.setStyle(
                "-fx-font-size: 15;"+
                        "-fx-text-fill: #ffffff;"+
                        "-fx-padding: 5;"+
                        "-fx-wrap-text: true;"                  // Va a capo
        );
        return errorLabel;
    }

    /**
     * Crea l'icona di avvertimento (punto esclamativo).
     *
     * @return Button contenente l'icona di avvertimento
     */
    public Button createWarningButton() {
        FontIcon warningIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        warningIcon.setIconSize(90);
        warningIcon.setIconColor(Color.ORANGE);
        Button warningButton = new Button();
        warningButton.setGraphic(warningIcon);
        warningButton.setStyle(
                "-fx-padding: 10;"+
                        "-fx-background-color: TRANSPARENT;"
        );
        return warningButton;
    }
}

