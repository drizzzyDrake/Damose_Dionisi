package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Classe layout per la mappa.
 * <p>
 * Gestisce la disposizione dei del layout della mappa
 * con bordi arrotondati tramite un clip e inserendo il nodo della mappa.
 */
public class MapsLayout {

    /**
     * Crea la root della mappa.
     *
     * @param map Nodo contenente la mappa da visualizzare
     * @return    StackPane che contiene la mappa con bordi arrotondati
     */
    public StackPane createMapsRoot(Node map) {
        StackPane mapsRoot = new StackPane();
        mapsRoot.getChildren().add(map);

        // Clip per bordi arrotondati.
        Rectangle clip = new Rectangle();
        clip.setArcWidth(40);                      // Arrotondamento orizzontale
        clip.setArcHeight(40);                     // Arrotondamento verticale

        // Associa le dimensioni del clip al layout.
        clip.widthProperty().bind(mapsRoot.widthProperty());
        clip.heightProperty().bind(mapsRoot.heightProperty());

        // Applica il clip al layout
        mapsRoot.setClip(clip);
        return mapsRoot;
    }
}

