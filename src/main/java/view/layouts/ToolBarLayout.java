package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Classe layout per la barra degli strumenti.
 * <p>
 * Gestisce la disposizone dei nodi della barra degli strumenti,
 * con sezione di scelta rapida tra fermate e linee preferite
 * e indicatore di connessione.
 */
public class ToolBarLayout {

    /**
     * Crea la root per la toolBar.
     *
     * @param favoriteListButton Bottone per accedere alla lista dei preferiti
     * @param connectivityButton Bottone per la gestione della connettività
     * @param legendButton       Bottone per la legenda
     * @param logoutButton       Bottone per il logout
     * @return                   VBox contenente i bottoni principali della toolbar
     */
    public VBox createToolBarRoot(Button favoriteListButton,
                                  Button connectivityButton,
                                  Button legendButton,
                                  Button logoutButton) {
        VBox toolBar = new VBox();
        toolBar.setStyle(
                "-fx-padding: 5;"+
                        "-fx-spacing: 10;"+
                        "-fx-alignment: TOP_RIGHT;"+
                        "-fx-background-color: TRANSPARENT;"
        );
        // Compone il layout con i rispettivi bottoni.
        toolBar.getChildren().addAll(favoriteListButton, connectivityButton, legendButton, logoutButton);
        return toolBar;
    }

    /**
     * Crea il layout per i bottoni di scelta tra fermate e linee.
     *
     * @param stopChoiseButton Bottone per la selezione delle fermate
     * @param lineChoiseButton Bottone per la selezione delle linee
     * @return                 HBox contenente i bottoni di scelta
     */
    public HBox createChoiseBox(Button stopChoiseButton,
                                Button lineChoiseButton) {
        HBox choiseBox = new HBox();
        choiseBox.setStyle(
                "-fx-spacing: 5;"
        );
        // Compone il layout con i rispettivi bottoni.
        choiseBox.getChildren().addAll(stopChoiseButton, lineChoiseButton);
        return choiseBox;
    }
}
