package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.feather.Feather;

/**
 * Classe node per la titlebar.
 * <p>
 * Fornisce i componenti grafici della titlebar.
 * </p>
 */
public class TitleBarNode {

    /**
     * Crea il bottone per ridurre la finestra a icona.
     *
     * @return Button con icona di minimizzazione
     */
    public Button createMinimizeButton() {
        FontIcon minimizeIcon = new FontIcon(Feather.MINUS);
        minimizeIcon.setIconSize(25);
        minimizeIcon.setIconColor(Color.ORANGE);
        Button minimizeButton = new Button();
        minimizeButton.setGraphic(minimizeIcon);
        minimizeButton.setStyle(
                "-fx-background-color: TRANSPARENT;"+
                        "-fx-cursor: hand;"
        );
        return minimizeButton;
    }

    /**
     * Crea il bottone per massimizzare la finestra.
     *
     * @return Button con icona di massimizzazione
     */
    public Button createMaximizeButton() {
        FontIcon maximizeIcon = new FontIcon(Feather.MAXIMIZE);
        maximizeIcon.setIconSize(20);
        maximizeIcon.setIconColor(Color.ORANGE);
        Button maximizeButton = new Button();
        maximizeButton.setGraphic(maximizeIcon);
        maximizeButton.setStyle(
                "-fx-background-color: TRANSPARENT;"+
                        "-fx-cursor: hand;"
        );
        return maximizeButton;
    }

    /**
     * Crea il bottone per chiudere la finestra.
     *
     * @return Button con icona di chiusura
     */
    public Button createCloseButton() {
        FontIcon closeIcon = new FontIcon(Feather.X);
        closeIcon.setIconSize(25);
        closeIcon.setIconColor(Color.ORANGE);
        Button closeButton = new Button();
        closeButton.setGraphic(closeIcon);
        closeButton.setStyle(
                "-fx-background-color: TRANSPARENT;"+
                        "-fx-cursor: hand;"
        );
        return closeButton;
    }
}
