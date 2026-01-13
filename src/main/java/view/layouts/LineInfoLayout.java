package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Classe layout per il pannello informativo delle linee.
 * <p>
 * Gestisce la disposizione dei nodi del pannello informativo delle linee
 * con label per il nome della linea, pulsanti per favoriti e tipo di linea,
 * e la selezione dell'orario.
 */
public class LineInfoLayout {

    /**
     * Crea la root del pannello informazioni linea.
     *
     * @return BorderPane root del pannello
     */
    public BorderPane createLineInfoRoot() {
        BorderPane lineInfoRoot = new BorderPane();
        lineInfoRoot.setStyle(
                "-fx-background-color: rgba(0,0,0,0.7);"+
                "-fx-text-fill: white;"+
                "-fx-background-radius: 30;"+
                "-fx-border-radius: 30;"+
                "-fx-padding: 10;"
        );

        VBox.setVgrow(lineInfoRoot, Priority.ALWAYS); // Espande il layout su tutta la box della griglia della home.
        return lineInfoRoot;
    }

    /**
     * Crea il layout per la zona superiore del pannello.
     *
     * @param lineTitleBox     HBox con il nome della linea e pulsanti correlati
     * @param selectionTimeBox HBox per la selezione dell'orario
     * @return                 VBox contenente le due HBox superiori
     */
    public VBox createTopBox(HBox lineTitleBox,
                             HBox selectionTimeBox) {
        VBox topBox = new VBox();
        topBox.getChildren().addAll(lineTitleBox, selectionTimeBox);
        return topBox;
    }

    /**
     * Crea il layout per la zona del nome della linea.
     *
     * @param favoriteButton  Pulsante per aggiungere/rimuovere dai preferiti
     * @param lineNameLabel   Label con il nome della linea
     * @param routeTypeButton Pulsante che indica il tipo di linea
     * @return                HBox contenente i componenti del titolo della linea
     */
    public HBox createLineTitleBox(Button favoriteButton,
                                   Label lineNameLabel,
                                   Button routeTypeButton) {
        HBox lineTitleBox = new HBox();
        lineTitleBox.setStyle(
                "-fx-alignment: CENTER_LEFT;"
        );

        lineTitleBox.getChildren().addAll(favoriteButton, lineNameLabel, routeTypeButton);
        return lineTitleBox;
    }

    /**
     * Crea il layout per la zona dell'orario.
     *
     * @param selectionTimeButton Pulsante per selezionare l'orario
     * @param nowButton           Pulsante per impostare l'orario corrente
     * @return                    HBox contenente i pulsanti di selezione orario
     */
    public HBox createSelectionTimeBox(Button selectionTimeButton,
                                       Button nowButton) {
        HBox selectionTimeBox = new HBox();
        selectionTimeBox.setStyle(
                "-fx-alignment: TOP_RIGHT;"+
                "-fx-padding: 10;"
        );

        selectionTimeBox.getChildren().addAll(selectionTimeButton, nowButton);
        return selectionTimeBox;
    }
}

