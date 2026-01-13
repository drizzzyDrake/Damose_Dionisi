package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Classe layout per la ricerca delle linee.
 * <p>
 * Gestisce la disposizione dei nodi della barra di ricerca delle linee
 * con TextField e Button, organizzati in StackPane e VBox.
 */
public class FindLineLayout {

    /**
     * Crea la root della barra di ricerca, usando un VBox.
     *
     * @param searchLinePane StackPane contenente i controlli della barra di ricerca
     * @return               VBox root della barra di ricerca
     */
    public VBox createSearchLineRoot(StackPane searchLinePane) {
        VBox searchLineRoot = new VBox(5);
        searchLineRoot.setStyle(
                "-fx-padding: 10;"+
                "-fx-background-color: TRANSPARENT;"
        );

        // Compone il layout con i rispettivi bottoni.
        searchLineRoot.getChildren().addAll(searchLinePane);
        return searchLineRoot;
    }

    /**
     * Crea il layout per la barra di ricerca vera e propria.
     *
     * @param searchLineField TextField per inserire il testo della ricerca
     * @param findLineButton  Button per avviare la ricerca
     * @return                StackPane contenente TextField e Button
     */
    public StackPane createSearchLinePane(TextField searchLineField,
                                          Button findLineButton) {
        StackPane searchLinePane = new StackPane();
        searchLinePane.setStyle(
                "-fx-alignment: CENTER_LEFT;"
        );
        // Compone il layout con i rispettivi bottoni.
        searchLinePane.getChildren().addAll(searchLineField, findLineButton);
        return searchLinePane;
    }
}
