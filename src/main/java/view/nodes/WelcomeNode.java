package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

import java.awt.*;

/**
 * Classe node per il messaggio di benvenuto.
 * <p>
 * Fornisce i componenti grafici del popup di benvenuto.
 * </p>
 */
public class WelcomeNode {

    /**
     * Crea la HBox per il messaggio di benvenuto.
     *
     * @return HBox formattata per mostrare il messaggio di benvenuto
     */
    public HBox createWelcomeBox(String currentUser) {
        HBox welcomeBox = new HBox();
        welcomeBox.setStyle(
                "-fx-background-color: rgba(0,0,0,0.6);"+
                        "-fx-text-fill: WHITE;"+
                        "-fx-border-radius: 10;"+
                        "-fx-background-radius: 15;"+
                        "-fx-font-size: 15;"+
                        "-fx-spacing: 5;"+
                        "-fx-padding: 10;"+
                        "-fx-alignment: CENTER;"
        );
        Image image = new Image(getClass().getResourceAsStream("/icons/damose-main.png"));
        ImageView welcomeImage = new ImageView(image);
        welcomeImage.setFitHeight(40);
        welcomeImage.setFitWidth(40);
        Label welcomeLabel = new Label("\nAccesso effettuato come " + currentUser);
        welcomeLabel.setTextFill(Paint.valueOf("WHITE"));
        welcomeBox.getChildren().addAll(welcomeImage, welcomeLabel);
        return welcomeBox;
    }
}
