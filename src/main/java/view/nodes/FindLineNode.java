package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.*;

/**
 * Classe node per la barra di ricerca delle linee.
 * <p>
 * Fornisce i componenti grafici della barra di ricerca delle linee.
 * </p>
 */
public class FindLineNode {

    // Nodi stilisticamente uguali a FindStopNode.
    private final FindStopNode findStopNode =  new FindStopNode();

    /**
     * Crea la barra di ricerca delle linee.
     *
     * @return TextField utilizzato per inserire il testo di ricerca
     */
    public TextField createSearchLineField() {
        TextField searchLineField = findStopNode.createSearchStopField();
        searchLineField.setPromptText("Cerca Linea...");
        return searchLineField;
    }

    /**
     * Crea il bottone con la lente di ingrandimento per avviare la ricerca.
     *
     * @return Button stilizzato per la ricerca delle linee
     */
    public Button createFindLineButton() {
        Button findLineButton = findStopNode.createFindStopButton();
        return findLineButton;
    }

    /**
     * Crea la tendina dei suggerimenti di ricerca.
     *
     * @return ListView contenente le possibili corrispondenze per la ricerca
     */
    public ListView<String> createSuggestionList() {
        ListView<String> suggestionList = findStopNode.createSuggestionList();
        return suggestionList;
    }
}

