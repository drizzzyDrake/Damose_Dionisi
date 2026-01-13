package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Classe node per la barra di ricerca delle fermate.
 * <p>
 * Fornisce i componenti grafici della barra di ricerca delle fermate.
 * </p>
 */
public class FindStopNode {

    /**
     * Crea la barra di ricerca delle fermate.
     *
     * @return TextField utilizzato per inserire il testo di ricerca
     */
    public TextField createSearchStopField() {
        TextField searchStopField = new TextField();
        searchStopField.setPromptText("Cerca fermata...");
        searchStopField.setStyle(
                "-fx-background-color: rgba(0,0,0,0.6);"+
                        "-fx-text-fill: white;"+
                        "-fx-background-radius: 30;"+
                        "-fx-border-radius: 30;"+
                        "-fx-cursor: text;"+
                        "-fx-padding: 10 15 10 35;"        // 30 da sinistra per lasciare spazio all'icona.
        );
        searchStopField.setPrefWidth(400);         // Imposta la larghezza del campo.
        return searchStopField;
    }

    /**
     * Crea il bottone con la lente di ingrandimento per avviare la ricerca.
     *
     * @return Button stilizzato per la ricerca delle fermate
     */
    public Button createFindStopButton() {
        Button findStopButton = new Button();
        FontIcon searchIcon = new FontIcon(Feather.SEARCH);
        searchIcon.setIconColor(Color.ORANGE);
        searchIcon.setIconSize(20);
        findStopButton.setGraphic(searchIcon);
        findStopButton.setStyle(
                "-fx-background-color: TRANSPARENT;"
        );
        return findStopButton;
    }

    /**
     * Crea la tendina dei suggerimenti di ricerca.
     * <p>
     * Ogni cella della lista mostra un'etichetta con testo troncato se necessario,
     * cambia colore al passaggio del mouse e alla selezione.
     * </p>
     *
     * @return ListView contenente le possibili corrispondenze per la ricerca
     */
    public ListView<String> createSuggestionList() {

        // Settaggio dello stile generale della listView.
        ListView<String> suggestionList = new ListView<>();
        suggestionList.setStyle(
                "-fx-background-color: rgba(0,0,0);"+
                        "-fx-text-fill: WHITE;"+
                        "-fx-font-size: 15;"+
                        "-fx-border-radius: 15;"+
                        "-fx-background-radius: 15;"
        );

        // Settaggio dello stile della listView cella per cella.
        suggestionList.setCellFactory(listView -> new ListCell<String>() {
            private final Label label = new Label();              // Crea un'etichetta all'interno di ogni cella

            {
                label.setTextOverrun(OverrunStyle.ELLIPSIS);     // Se il testo è troppo lungo, mostra "..."
                label.setWrapText(false);                        // No a capo
                label.maxWidthProperty()
                        .bind(listView
                                .widthProperty()
                                .subtract(20));                 // Non oltre la larghezza della ListView -20 px
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                label.setText(item);
                setGraphic(label);

                // Stile dinamico selezione.
                if (isSelected()) {
                    setStyle(
                            "-fx-background-color: #ffa31a;"+
                                    "-fx-border-radius: 15;"+
                                    "-fx-background-radius: 15;"
                    );
                    label.setTextFill(Color.BLACK);
                } else {
                    setStyle("-fx-background-color: TRANSPARENT;");
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

        return suggestionList;
    }
}


