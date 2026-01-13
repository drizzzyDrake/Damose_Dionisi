package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Classe layout per il pannello informativo delle fermate.
 * <p>
 * Gestisce la disposizione dei nodi del pannello informativo delle fermate,
 * con label per il nome della fermata, pulsanti per favoriti
 * e la selezione dell'orario.
 */
public class StopInfoLayout {

    /**
     * Crea la root del pannello informativo delle fermate.
     *
     * @return BorderPane root del pannello
     */
    public BorderPane createStopInfoRoot() {
        BorderPane stopInfoRoot = new BorderPane();
        stopInfoRoot.setStyle(
                "-fx-background-color: rgba(0,0,0,0.7);"+
                "-fx-text-fill: white;"+
                "-fx-background-radius: 30;"+
                "-fx-border-radius: 30;"+
                "-fx-padding: 10;"
        );

        VBox.setVgrow(stopInfoRoot, Priority.ALWAYS);               // Espande il layout su tutta la box della griglia della home.
        return stopInfoRoot;
    }

    /**
     * Crea il layout per la zona superiore del pannello.
     *
     * @param stopTitleBox     HBox contenente il nome e pulsanti della fermata
     * @param selectionTimeBox HBox contenente i pulsanti per la selezione dell'orario
     * @return                 VBox contenente le due zone superiori
     */
    public VBox createTopBox(HBox stopTitleBox,
                             HBox selectionTimeBox) {
        VBox topBox = new VBox();
        // Compone il layout con i rispettivi bottoni.
        topBox.getChildren().addAll(stopTitleBox, selectionTimeBox);
        return topBox;
    }

    /**
     * Crea il layout per la zona del nome della fermata.
     *
     * @param favoriteButton Bottone per aggiungere la fermata ai preferiti
     * @param stopNameLabel  Label con il nome della fermata
     * @return               HBox contenente il nome della fermata e il bottone dei preferiti
     */
    public HBox createStopTitleBox(Button favoriteButton,
                                   Label stopNameLabel) {
        HBox stopTitleBox = new HBox();
        stopTitleBox.setStyle(
                "-fx-alignment: CENTER_LEFT;"
        );

        // Compone il layout con i rispettivi bottoni.
        stopTitleBox.getChildren().addAll(favoriteButton, stopNameLabel);
        return stopTitleBox;
    }

    /**
     * Crea il layout per la zona dell'orario.
     *
     * @param selectionTimeButton Bottone per selezionare l'orario
     * @param nowButton           Bottone per selezionare l'orario corrente
     * @return                    HBox contenente i pulsanti dell'orario
     */
    public HBox createSelectionTimeBox(Button selectionTimeButton,
                                       Button nowButton) {
        HBox selectionTimeBox = new HBox();
        selectionTimeBox.setStyle(
                "-fx-alignment: TOP_RIGHT;"+
                "-fx-padding: 10;"
        );

        // Compone il layout con i rispettivi bottoni.
        selectionTimeBox.getChildren().addAll(selectionTimeButton, nowButton);
        return selectionTimeBox;
    }
}


