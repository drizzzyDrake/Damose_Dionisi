package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import model.Arrival;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import java.awt.*;

/**
 * Classe node per il pannello informativo delle linee.
 * <p>
 * Fornisce i componenti grafici del pannello informativo delle linee.
 * </p>
 */
public class LineInfoNode {

    // Per nodi stilisticamente uguali a StopInfoNode.
    private final StopInfoNode stopInfoNode =  new StopInfoNode();

    // Colore del tipo di linea.
    private Color typeColor;

    /**
     * Crea l'etichetta per il nome della linea.
     *
     * @return Label per il nome della linea
     */
    public Label createLineNameLabel(){
        Label lineNameLabel = stopInfoNode.createStopNameLabel();
        return lineNameLabel;
    }

    /**
     * Crea il bottone per aggiungere/rimuovere la linea dai preferiti.
     *
     * @return Button per gestire i preferiti
     */
    public Button createFavoriteButton() {
        Button favoriteButton = stopInfoNode.createFavoriteButton();
        return favoriteButton;
    }

    /**
     * Imposta lo stato del bottone dei preferiti modificando il colore.
     *
     * @param button bottone dei preferiti
     * @param active true se la linea è nei preferiti, false altrimenti
     */
    public void setFavoriteButtonState(Button button, boolean active) {
        FontIcon icon = (FontIcon) button.getGraphic();
        icon.setIconColor(active ? Color.ORANGE : Color.GRAY);      // Colore in base allo stato della linea.
    }

    /**
     * Crea il bottone che indica il tipo di linea.
     *
     * @return Button per il tipo di linea
     */
    public Button createRouteTypeButton() {
        Button routeTypeButton = new Button();
        routeTypeButton.setStyle(
                "-fx-background-color: TRANSPARENT"
        );
        return routeTypeButton;
    }

    /**
     * Imposta lo stato del bottone del tipo di linea con icona e colore appropriati.
     *
     * @param button    bottone da aggiornare
     * @param routeType tipo di linea (0=Tram, 1=Metro, 3=Bus, altri=default)
     */
    public void setRouteTypeButtonState(Button button, int routeType) {
        if (routeType == 0) {
            FontIcon tramIcon = new FontIcon(FontAwesomeSolid.TRAIN);
            typeColor = Color.RED;
            tramIcon.setIconColor(typeColor);
            tramIcon.setIconSize(20);
            button.setGraphic(tramIcon);
        }
        else if (routeType == 1) {
            FontIcon metroIcon = new FontIcon(FontAwesomeSolid.SUBWAY);
            typeColor = Color.DEEPSKYBLUE;
            metroIcon.setIconColor(typeColor);
            metroIcon.setIconSize(20);
            button.setGraphic(metroIcon);
        }
        else if (routeType == 3) {
            FontIcon busIcon = new FontIcon(FontAwesomeSolid.BUS);
            typeColor = Color.DEEPPINK;
            busIcon.setIconColor(typeColor);
            busIcon.setIconSize(20);
            button.setGraphic(busIcon);
        }
        else {
            FontIcon nullIcon = new FontIcon(FontAwesomeSolid.BORDER_NONE);
            typeColor = Color.DARKGRAY;
            nullIcon.setIconColor(typeColor);
            nullIcon.setIconSize(20);
            button.setGraphic(nullIcon);
        }
    }

    /**
     * Crea il bottone per la selezione dell'orario.
     *
     * @return Button per la selezione dell'orario
     */
    public Button createSelectionTimeButton() {
        Button selectionTimeButton = stopInfoNode.createSelectionTimeButton();
        selectionTimeButton.setText("Orario");
        return selectionTimeButton;
    }

    /**
     * Crea la lista dei possibili orari per la linea.
     *
     * @return ListView contenente gli orari
     */
    public ListView<String> createTimesList() {
        ListView<String> timesList = stopInfoNode.createTimesList();
        return timesList;
    }

    /**
     * Crea il bottone per selezionare l'orario attuale.
     *
     * @return Button per selezionare l'orario corrente
     */
    public Button createNowButton() {
        Button nowButton = stopInfoNode.createNowButton();
        return nowButton;
    }

    /**
     * Crea la lista delle fermate che tocca la corsa.
     *
     * @return ListView contenente le fermate
     */
    public ListView<Arrival> createStopsList() {
        ListView<Arrival> stopsList = new ListView<>();
        stopsList.setStyle(
                "-fx-background-color: rgba(0,0,0,0.5);" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;"
        );

        Platform.runLater(() -> {
            ScrollBar vBar = (ScrollBar) stopsList.lookup(".scroll-bar:vertical");
            if (vBar != null) {
                vBar.setStyle(
                        "-fx-background-color: TRANSPARENT;" +
                                "-fx-pref-width: 0;" +
                                "-fx-opacity: 0;"
                );
            }
        });

        stopsList.setCellFactory(_ -> new ListCell<Arrival>() {
            @Override
            protected void updateItem(Arrival item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(
                            "-fx-background-color: TRANSPARENT; " +
                                    "-fx-text-fill: WHITE;"
                    );
                } else {
                    // Nome fermata e ID fermata.
                    Label textLabel = new Label(item.getTripHeadsign() + " [" + item.getStopId() + "]");
                    textLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
                    textLabel.setWrapText(false);
                    textLabel.maxWidthProperty()
                            .bind(stopsList
                                    .widthProperty()
                                    .subtract(150));
                    textLabel.setStyle(
                            "-fx-text-fill: WHITE;"
                    );

                    // Orario formattato.
                    Label timeLabel = new Label(item.getFormattedTime());
                    timeLabel.setStyle(
                            "-fx-text-fill: GRAY;"
                    );

                    // Ritardo/anticipo.
                    Label delayLabel = new Label();
                    if (item.getDelayMinutes() != null) {
                        long delay = item.getDelayMinutes();
                        if (delay > 0) {
                            delayLabel.setText("+" + delay + "'");
                            delayLabel.setStyle(
                                    "-fx-text-fill: RED;"
                            );
                        } else if (delay < 0) {
                            delayLabel.setText(delay + "'");
                            delayLabel.setStyle(
                                    "-fx-text-fill: GREEN;"
                            );
                        } else {
                            delayLabel.setText("on time");
                            delayLabel.setStyle(
                                    "-fx-text-fill: GRAY;"
                            );
                        }
                    }

                    // Indicatore posizione mezzo.
                    Label dot = new Label("●");
                    switch (item.getStatus()) {
                        case NEXT -> dot.setTextFill(typeColor);
                        case FUTURE -> dot.setTextFill(typeColor.deriveColor(0, 1, 1, 0.4));
                        case PAST -> dot.setTextFill(typeColor.deriveColor(0, 1, 1, 0.2));
                    }

                    // Costruzione riga.
                    HBox box = new HBox(dot, textLabel, timeLabel, delayLabel);
                    box.setSpacing(8);

                    setGraphic(box);
                    setText(null);
                    setStyle(
                            "-fx-background-color: TRANSPARENT;"
                    );

                    // Hover.
                    setOnMouseEntered(e -> {
                        if (!isEmpty()) textLabel.setTextFill(Color.web("#ffa31a"));
                    });
                    setOnMouseExited(e -> {
                        if (!isEmpty()) textLabel.setTextFill(Color.WHITE);
                    });
                }
            }
        });

        return stopsList;
    }
}
