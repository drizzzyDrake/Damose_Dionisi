package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

/**
 * Classe layout per la finestra di caricamento.
 * <p>
 * Gestisce la disposizione dei nodi della finestra di caricamento,
 * con un indicatore di progresso e un'etichetta descrittiva.
 */
public class LoadingLayout {

    /**
     * Crea la root della finestra di caricamento.
     *
     * @param loadingProgressIndicator Indicatore di progresso da mostrare
     * @param loadingLabel             Label descrittiva del caricamento
     * @return                         VBox contenente l'indicatore e la label
     */
    public VBox createLoadingRoot(ProgressIndicator loadingProgressIndicator,
                                  Label loadingLabel){
        VBox loadingRoot = new VBox(15);
        loadingRoot.setStyle(
                "-fx-background-color: rgba(0,0,0,0.8);"+
                "-fx-padding: 40;"+
                "-fx-background-radius: 20;"+
                "-fx-alignment: CENTER;"
        );
        // Compone il layout con i rispettivi componenti.
        loadingRoot.getChildren().addAll(loadingProgressIndicator, loadingLabel);
        return loadingRoot;
    }
}
