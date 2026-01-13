package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

/**
 * Classe node per la finestra di caricamento.
 * <p>
 * Fornisce i componenti grafici della finestra di caricamento.
 * </p>
 */
public class LoadingNode {

    /**
     * Crea l'etichetta per i messaggi di caricamento.
     *
     * @return Label per visualizzare messaggi di caricamento
     */
    public Label createLoadingLabel() {
        Label loadingLabel = new Label();
        loadingLabel.setStyle(
                "-fx-font-size: 15;"+
                        "-fx-text-fill: #ffffff;"+
                        "-fx-text-alignment: CENTER;"
        );
        return loadingLabel;
    }

    /**
     * Crea l'indicatore di progresso dinamico per il caricamento.
     *
     * @return ProgressIndicator per mostrare lo stato di caricamento
     */
    public ProgressIndicator createLoadingProgressIndicator() {
        ProgressIndicator loadingProgressIndicator = new ProgressIndicator();
        loadingProgressIndicator.setStyle(
                "-fx-progress-color: #ffa31a;"
        );
        return loadingProgressIndicator;
    }
}

