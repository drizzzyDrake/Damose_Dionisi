package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Classe node per la barra degli strumenti.
 * <p>
 * Fornisce i componenti grafici della barra degli strumenti.
 * </p>
 */
public class ToolBarNode {

    /**
     * Crea il bottone che mostra lo stato di connessione.
     *
     * @return Button vuoto, pronto per essere configurato con lo stato
     */
    public Button createConnectivityButton() {
        Button connectivityButton = new Button();
        connectivityButton.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.7);"+
                        "-fx-background-radius: 15;"
        );
        return connectivityButton;
    }

    /**
     * Setta lo stato del bottone della connessione.
     *
     * @param button   Bottone da aggiornare
     * @param isOnline True se la connessione è attiva, false altrimenti
     */
    public void setConnectivityButtonState(Button button, boolean isOnline) {
        if (isOnline) {
            FontIcon onlineIcon = new FontIcon(Feather.WIFI);
            onlineIcon.setIconColor(Color.LIGHTGREEN);
            onlineIcon.setIconSize(30);
            button.setGraphic(onlineIcon);
            Tooltip tooltip = new Tooltip("ONLINE");
            button.setTooltip(tooltip);
        } else {
            FontIcon onlineIcon = new FontIcon(Feather.WIFI_OFF);
            onlineIcon.setIconColor(Color.RED);
            onlineIcon.setIconSize(30);
            button.setGraphic(onlineIcon);
            Tooltip tooltip = new Tooltip("OFFLINE");
            button.setTooltip(tooltip);
        }
    }

    /**
     * Crea il bottone per visualizzare la lista dei preferiti.
     *
     * @return Button con icona a stella
     */
    public Button createFavoriteListButton() {
        Button favoriteListButton = new Button();
        FontIcon starIcon = new FontIcon(Feather.STAR);
        starIcon.setIconColor(Color.ORANGE);
        starIcon.setIconSize(30);
        favoriteListButton.setGraphic(starIcon);
        favoriteListButton.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.7);"+
                        "-fx-cursor: hand;"+
                        "-fx-background-radius: 15;"
        );
        return favoriteListButton;
    }

    /**
     * Crea il bottone per la scelta tra fermate e linee.
     *
     * @param choiseText Testo da mostrare sul bottone
     * @return           Button configurato con testo e stile
     */
    public Button createChoiseButton(String choiseText) {
        Button choiseButton = new Button(choiseText);
        choiseButton.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.7);"+
                        "-fx-cursor: hand;"+
                        "-fx-background-radius: 15;"+
                        "-fx-text-fill: #ffa31a;"+
                        "-fx-font-size: 15;"
        );
        choiseButton.setPrefSize(147.5, 38);
        return choiseButton;
    }

    /**
     * Crea la tendina (ListView) degli orari della fermata.
     *
     * @return ListView configurata con stile dinamico e celle personalizzate
     */
    public ListView<String> createFavoriteList() {
        ListView<String> favoriteList = new ListView<>();

        // Settaggio dello stile generale della listView.
        favoriteList.setStyle(
                "-fx-background-color: rgba(0,0,0,0.8);"+
                        "-fx-text-fill: WHITE;"+
                        "-fx-border-radius: 15;"+
                        "-fx-background-radius: 15;"+
                        "-fx-font-size: 15;"
        );

        favoriteList.setPrefSize(297.5, 200);

        // Settaggio dello stile della listView cella per cella.
        favoriteList.setCellFactory(listView -> new ListCell<String>() {
            private final Label label = new Label();
            {
                label.setTextOverrun(OverrunStyle.ELLIPSIS);
                label.setWrapText(false);
                label.maxWidthProperty()
                        .bind(listView
                                .widthProperty()
                                .subtract(20));
            }

            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                label.setText(item);
                setGraphic(label);

                // Stile dinamico selezione.
                if (isSelected()) {
                    setStyle(
                            "-fx-background-color: #ffa31a;"+
                                    " -fx-border-radius: 15;"+
                                    " -fx-background-radius: 15;"
                    );
                    label.setTextFill(Color.BLACK);
                } else {
                    setStyle(
                            "-fx-background-color: TRANSPARENT;"
                    );
                    label.setTextFill(Color.WHITE);
                }
            }
            {
                setOnMouseEntered(e -> {
                    if (!isEmpty()) label.setTextFill(Color.web("#ffa31a"));
                });
                setOnMouseExited(e -> {
                    if (!isEmpty()) label.setTextFill(Color.WHITE);
                });
            }
        });

        return favoriteList;
    }

    /**
     * Crea il bottone per visualizzare la legenda.
     *
     * @return Button con icona a punto interrogativo
     */
    public Button createLegendButton() {
        Button legendButton = new Button();
        FontIcon questionIcon = new FontIcon(FontAwesomeSolid.QUESTION_CIRCLE);
        questionIcon.setIconColor(Color.rgb(255,163,26,0.8));
        questionIcon.setIconSize(30);
        legendButton.setGraphic(questionIcon);
        legendButton.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.7);"+
                        "-fx-cursor: hand;"+
                        "-fx-background-radius: 15;"
        );
        return legendButton;
    }

    /**
     * Crea la box della legenda.
     *
     * @return VBox con informazioni sull'app
     */
    public VBox createLegendBox() {
        VBox legendBox = new VBox();
        legendBox.setStyle(
                "-fx-background-color: rgba(0,0,0,0.8);"+
                        "-fx-text-fill: WHITE;"+
                        "-fx-border-radius: 15;"+
                        "-fx-background-radius: 15;"+
                        "-fx-font-size: 15;"+
                        "-fx-spacing: 10;"+
                        "-fx-padding: 10;"
        );
        legendBox.setPrefWidth(200);

        Label greenDot = new Label("●");
        greenDot.setTextFill(Color.GREEN);

        Label grayNextDot = new Label("●");
        grayNextDot.setTextFill(Color.rgb(126,126,126));

        Label grayFutureDot = new Label("●");
        grayFutureDot.setTextFill(Color.rgb(126,126,126, 0.4));

        Label grayPastDot = new Label("●");
        grayPastDot.setTextFill(Color.rgb(126,126,126,0.2));

        FontIcon tramIcon = new FontIcon(FontAwesomeSolid.TRAIN);
        tramIcon.setIconColor(Color.RED);
        tramIcon.setIconSize(15);

        FontIcon metroIcon = new FontIcon(FontAwesomeSolid.SUBWAY);
        metroIcon.setIconColor(Color.DEEPSKYBLUE);
        metroIcon.setIconSize(15);

        FontIcon busIcon = new FontIcon(FontAwesomeSolid.BUS);
        busIcon.setIconColor(Color.DEEPPINK);
        busIcon.setIconSize(15);

        Label tramLabel = new Label(" : Tram");
        tramLabel.setTextFill(Color.WHITE);
        HBox tramBox = new HBox(tramIcon, tramLabel);

        Label metroLabel = new Label(" : Metro");
        metroLabel.setTextFill(Color.WHITE);
        HBox metroBox = new HBox(metroIcon, metroLabel);

        Label busLabel = new Label(" : Bus");
        busLabel.setTextFill(Color.WHITE);
        HBox busBox = new HBox(busIcon, busLabel);

        Separator separator = new Separator();

        Label realTimeLabel = new Label(" : Dato real time");
        realTimeLabel.setTextFill(Color.WHITE);
        HBox realTimeBox = new HBox(greenDot, realTimeLabel);

        Label nextLabel = new Label(" : Prossima fermata");
        nextLabel.setTextFill(Color.WHITE);
        HBox nextBox = new HBox(grayNextDot, nextLabel);

        Label futureLabel = new Label(" : Fermata futura");
        futureLabel.setTextFill(Color.WHITE);
        HBox futureBox = new HBox(grayFutureDot, futureLabel);

        Label pastLabel = new Label(" : Fermata passata");
        pastLabel.setTextFill(Color.WHITE);
        HBox pastBox = new HBox(grayPastDot, pastLabel);

        legendBox.getChildren().addAll(tramBox, metroBox, busBox, separator, realTimeBox, nextBox, futureBox, pastBox);
        return legendBox;
    }

    /**
     * Crea il bottone per il logout.
     *
     * @return Button con icona di uscita
     */
    public Button createLogoutButton() {
        Button logoutButton = new Button();
        FontIcon logoutIcon = new FontIcon(Feather.LOG_OUT);
        logoutIcon.setIconColor(Color.rgb(255,163,26,0.8));
        logoutIcon.setIconSize(30);
        logoutButton.setGraphic(logoutIcon);
        logoutButton.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.7);"+
                        "-fx-cursor: hand;"+
                        "-fx-background-radius: 15;"
        );
        Tooltip tooltip = new Tooltip("Logout");
        logoutButton.setTooltip(tooltip);
        return logoutButton;
    }
}

