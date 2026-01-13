package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import model.Arrival;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import java.awt.*;

/**
 * Classe node per il pannello informativo delle fermate.
 * <p>
 * Fornisce i componenti grafici per il pannello informativo delle fermate.
 * </p>
 */
public class StopInfoNode {

    /**
     * Crea l'etichetta per il nome della fermata.
     *
     * @return Label per visualizzare il nome della fermata
     */
    public Label createStopNameLabel() {
        Label stopNameLabel = new Label();
        stopNameLabel.setStyle(
                "-fx-text-fill: #ffa31a;"+
                        "-fx-font-size: 17;"
        );
        return stopNameLabel;
    }

    /**
     * Crea il bottone per aggiungere o rimuovere la fermata dai preferiti.
     *
     * @return Button con icona a stella
     */
    public Button createFavoriteButton() {
        Button favoriteButton = new Button();
        FontIcon starIcon = new FontIcon(Feather.STAR);
        starIcon.setIconColor(Color.GRAY); // Colore iniziale
        starIcon.setIconSize(20);
        favoriteButton.setGraphic(starIcon);
        favoriteButton.setStyle(
                "-fx-background-color: TRANSPARENT;"+
                        "-fx-cursor: hand;"
        );
        return favoriteButton;
    }

    /**
     * Modifica lo stato del bottone dei preferiti cambiando il colore.
     *
     * @param button Button da aggiornare
     * @param active true se attivo, false altrimenti
     */
    public void setFavoriteButtonState(Button button, boolean active) {
        FontIcon icon = (FontIcon) button.getGraphic();
        icon.setIconColor(active ? Color.ORANGE : Color.GRAY);
    }

    /**
     * Crea il bottone per selezionare l'orario di partenza.
     *
     * @return Button per selezionare l'orario
     */
    public Button createSelectionTimeButton() {
        Button selectionTimeButton = new Button("Partenza");
        selectionTimeButton.setStyle(
                "-fx-background-color: rgba(0,0,0,0.5);"+
                        "-fx-text-fill: #ffa31a;"+
                        "-fx-background-radius: 15;"+
                        "-fx-cursor: hand;"
        );
        return selectionTimeButton;
    }

    /**
     * Crea la tendina (ListView) degli orari della fermata.
     *
     * @return ListView&lt;String&gt; con gli orari
     */
    public ListView<String> createTimesList() {
        ListView<String> timesList = new ListView<>();
        timesList.setStyle(
                "-fx-background-color: rgba(0,0,0,0.7);"+
                        "-fx-text-fill: WHITE;"+
                        "-fx-border-radius: 15;"+
                        "-fx-background-radius: 15;"+
                        "-fx-font-size: 15;"
        );
        timesList.setPrefSize(55, 100);

        timesList.setCellFactory(_ -> {
            ListCell<String> cell = new ListCell<>() {
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                    if (isSelected()) {
                        setStyle(
                                "-fx-background-color: #ffa31a;"+
                                        "-fx-text-fill: BLACK;"+
                                        "-fx-background-radius: 15;"+
                                        "-fx-border-radius: 15;"
                        );
                    } else {
                        setStyle(
                                "-fx-background-color: TRANSPARENT;"+
                                        "-fx-text-fill: WHITE;"
                        );
                    }
                }
            };

            cell.setOnMouseEntered(_ -> {
                if (!cell.isEmpty()) {
                    cell.setStyle(
                            "-fx-background-color: TRANSPARENT;"+
                                    "-fx-text-fill: #ffa31a;"
                    );
                }
            });

            cell.setOnMouseExited(_ -> {
                if (!cell.isEmpty()) {
                    cell.setStyle(
                            "-fx-background-color: TRANSPARENT;"+
                                    "-fx-text-fill: WHITE;"
                    );
                }
            });

            return cell;
        });

        return timesList;
    }

    /**
     * Crea il bottone per selezionare l'orario attuale.
     *
     * @return Button per selezionare l'orario corrente
     */
    public Button createNowButton() {
        Button nowButton = new Button("Ora attuale");
        nowButton.setStyle(
                "-fx-text-fill: #ffa31a;"+
                        "-fx-background-color: TRANSPARENT;"+
                        "-fx-cursor: hand;"
        );
        return nowButton;
    }

    /**
     * Crea la lista delle linee in arrivo in fermata.
     *
     * @return ListView contenente le linee
     */
    public ListView<Arrival> createArrivalsList() {
        ListView<Arrival> arrivalsList = new ListView<>();
        arrivalsList.setStyle(
                "-fx-background-color: rgba(0,0,0,0.5);" +
                        "-fx-text-fill: WHITE;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;"
        );

        Platform.runLater(() -> {
            ScrollBar vBar = (ScrollBar) arrivalsList.lookup(".scroll-bar:vertical");
            if (vBar != null) {
                vBar.setStyle(
                        "-fx-background-color: TRANSPARENT;" +
                                "-fx-pref-width: 0;" +
                                "-fx-opacity: 0;"
                );
            }
        });

        arrivalsList.setCellFactory(_ -> new ListCell<Arrival>() {
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
                    // ID linea e destinazione.
                    Label textLabel = new Label(item.getRouteId() + " → " + item.getTripHeadsign());
                    textLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
                    textLabel.setWrapText(false);
                    textLabel.maxWidthProperty()
                            .bind(arrivalsList
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

                    // Indicatore realtime.
                    Label dot = new Label("●");
                    dot.setStyle(item.isRealtime()
                            ? "-fx-text-fill: GREEN; -fx-font-size: 14px;"
                            : "-fx-text-fill: TRANSPARENT; -fx-font-size: 14px;");

                    // Costruzione riga.
                    HBox box = new HBox(textLabel, timeLabel, delayLabel, dot);
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

        return arrivalsList;
    }
}


