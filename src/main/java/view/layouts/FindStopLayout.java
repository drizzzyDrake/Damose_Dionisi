package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Classe layout per la ricerca delle fermate.
 * <p>
 * Gestisce la disposizione dei nodi della barra di ricerca delle fermate
 * con TextField e Button, organizzati in StackPane e VBox.
 */
public class FindStopLayout {

    /**
     * Crea la root della barra di ricerca delle fermate, usando un VBox.
     *
     * @param searchStopPane StackPane contenente i controlli della barra di ricerca
     * @return               VBox root della barra di ricerca delle fermate
     */
    public VBox createSearchStopRoot(StackPane searchStopPane) {
        VBox searchStopRoot = new VBox(5);
        searchStopRoot.setStyle(
                "-fx-padding: 10;"+
                "-fx-background-color: TRANSPARENT;"
        );

        // Compone il layout con i rispettivi bottoni.
        searchStopRoot.getChildren().addAll(searchStopPane);
        return searchStopRoot;
    }

    /**
     * Crea il layout per la barra di ricerca delle fermate.
     *
     * @param searchStopField TextField per inserire il testo della ricerca della fermata
     * @param findStopButton  Button per avviare la ricerca della fermata
     * @return                StackPane contenente TextField e Button
     */
    public StackPane createSearchStopPane(TextField searchStopField,
                                          Button findStopButton) {
        StackPane searchStopPane = new StackPane();
        searchStopPane.setStyle(
                "-fx-alignment: CENTER_LEFT;"
        );
        // Compone il layout con i rispettivi bottoni.
        searchStopPane.getChildren().addAll(searchStopField, findStopButton);
        return searchStopPane;
    }
}


