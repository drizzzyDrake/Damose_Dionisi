package model;

/**
 * JavaBean che rappresenta un punto geografico di una linea di trasporto pubblico.
 * <p>
 * Contiene tutte le informazioni sulla posizione del punto sulla mappa (GTFS {@code shapes.txt}).
 * </p>
 * <ul>
 *     <li>{@code shapeId} - identificatore univoco del percorso</li>
 *     <li>{@code shapePtLat} - latitudine del punto (in gradi decimali)</li>
 *     <li>{@code shapePtLon} - longitudine del punto (in gradi decimali)</li>
 *     <li>{@code shapePtSequence} - ordine del punto lungo il percorso</li>
 *     <li>{@code shapeDistTraveled} - distanza percorsa cumulativa in metri</li>
 * </ul>
 */
public class ShapePoint {
    private String shapeId;
    private Double shapePtLat;
    private Double shapePtLon;
    private Integer shapePtSequence;    // Ordine del punto lungo il percorso.
    private Double shapeDistTraveled;

    /**
     * Costruttore vuoto.
     */
    public ShapePoint() {
        // Costruttore vuoto richiesto da Apache Commons CSV, che crea oggetti e poi li riempie con i setter.
    }

    /**
     * Restituisce l'identificatore univoco del percorso.
     *
     * @return identificatore univoco del percorso (può essere {@code null})
     */
    public String getShapeId() {
        return shapeId;
    }

    /**
     * Imposta l'identificatore univoco del percorso.
     *
     * @param shapeId identificatore univoco del percorso
     */
    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    /**
     * Restituisce la latitudine del punto.
     *
     * @return latitudine del punto (può essere {@code null})
     */
    public Double getShapePtLat() {
        return shapePtLat;
    }

    /**
     * Imposta la latitudine del punto.
     *
     * @param shapePtLat latitudine del punto
     */
    public void setShapePtLat(Double shapePtLat) {
        this.shapePtLat = shapePtLat;
    }

    /**
     * Restituisce la longitudine del punto.
     *
     * @return longitudine del punto (può essere {@code null})
     */
    public Double getShapePtLon() {
        return shapePtLon;
    }

    /**
     * Imposta la longitudine del punto.
     *
     * @param shapePtLon longitudine del punto
     */
    public void setShapePtLon(Double shapePtLon) {
        this.shapePtLon = shapePtLon;
    }

    /**
     * Restituisce l'ordine del punto lungo il percorso.
     *
     * @return ordine del punto lungo il percorso (può essere {@code null})
     */
    public Integer getShapePtSequence() {
        return shapePtSequence;
    }

    /**
     * Imposta l'ordine del punto lungo il percorso.
     *
     * @param shapePtSequence ordine del punto lungo il percorso
     */
    public void setShapePtSequence(Integer shapePtSequence) {
        this.shapePtSequence = shapePtSequence;
    }

    /**
     * Restituisce la distanza percorsa cumulativa.
     *
     * @return distanza percorsa cumulativa (può essere {@code null})
     */
    public Double getShapeDistTraveled() {
        return shapeDistTraveled;
    }

    /**
     * Imposta la distanza percorsa cumulativa.
     *
     * @param shapeDistTraveled distanza percorsa cumulativa
     */
    public void setShapeDistTraveled(Double shapeDistTraveled) {
        this.shapeDistTraveled = shapeDistTraveled;
    }
}


