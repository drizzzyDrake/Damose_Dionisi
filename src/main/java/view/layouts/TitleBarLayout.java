package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Classe layout per la titleBar.
 * <p>
 * Gestisce la disposizionedei nodi della barra del titolo,
 * con i pulsanti di minimizzazione, massimizzazione e chiusura della finestra.
 */
public class TitleBarLayout {

    /**
     * Crea il layout per la titleBar.
     *
     * @param minimizeButton Bottone per minimizzare la finestra
     * @param maximizeButton Bottone per massimizzare/ripristinare la finestra
     * @param closeButton    Bottone per chiudere la finestra
     * @return               HBox contenente i pulsanti della titleBar
     */
    public HBox createTitleBarBox(Button minimizeButton,
                                  Button maximizeButton,
                                  Button closeButton) {
        HBox titleBar = new HBox();
        titleBar.setStyle(
                "-fx-padding: 5;"+
                "-fx-alignment: CENTER_RIGHT;"+
                "-fx-background-color: TRANSPARENT;"
        );
        // Compone il layout con i rispettivi bottoni.
        titleBar.getChildren().addAll(minimizeButton, maximizeButton, closeButton);
        return titleBar;
    }
}
