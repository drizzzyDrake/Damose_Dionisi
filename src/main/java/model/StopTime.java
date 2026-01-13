package model;

/**
 * JavaBean che rappresenta il passaggio di una corsa in una fermata.
 * <p>
 * Contiene tutte le informazioni principali sul passaggo di una corsa sulla fermata (GTFS {@code stop_times.txt}).
 * </p>
 * <ul>
 *     <li>{@code tripId} - corsa di passaggio</li>
 *     <li>{@code arrivalTime} - orario di arrivo alla fermata (formato HH:MM:SS)</li>
 *     <li>{@code departureTime} - orario di partenza dalla fermata (formato HH:MM:SS)</li>
 *     <li>{@code stopId} - fermata</li>
 *     <li>{@code stopSequence} - ordine della fermata nella corsa</li>
 *     <li>{@code stopHeadsign} - destinazione mostrata sul veicolo</li>
 *     <li>{@code pickupType} - modalità di salita (0 = normale, 1 = non permessa, 2 = su prenotazione, 3 = contattare l’azienda)</li>
 *     <li>{@code dropOffType} - modalità di discesa (stessi valori di pickupType)</li>
 *     <li>{@code shapeDistTraveled} - distanza percorsa lungo la shape fino alla fermata</li>
 *     <li>{@code timepoint} - indica se l’orario è preciso (1) o approssimativo (0)</li>
 * </ul>
 */
public class StopTime {
    private String tripId;
    private String arrivalTime;
    private String departureTime;
    private String stopId;
    private Integer stopSequence;
    private String stopHeadsign;
    private Integer pickupType;
    private Integer dropOffType;
    private Double shapeDistTraveled;
    private Integer timepoint;

    /**
     * Costruttore vuoto.
     */
    public StopTime() {
        // Costruttore vuoto richiesto da Apache Commons CSV, che crea oggetti e poi li riempie con i setter.
    }

    /**
     * Restituisce l'identificatore della corsa.
     *
     * @return identificatore della corsa (può essere {@code null})
     */
    public String getTripId() {
        return tripId;
    }

    /**
     * Imposta l'identificatore della corsa.
     *
     * @param tripId identificatore della corsa
     */
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    /**
     * Restituisce l'orario di arrivo.
     *
     * @return orario di arrivo (può essere {@code null})
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Imposta l'orario di arrivo.
     *
     * @param arrivalTime orario di arrivo
     */
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Restituisce l'orario di partenza.
     *
     * @return orario di partenza (può essere {@code null})
     */
    public String getDepartureTime() {
        return departureTime;
    }

    /**
     * Imposta l'orario di partenza.
     *
     * @param departureTime orario di partenza
     */
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    /**
     * Restituisce l'identificatore della fermata.
     *
     * @return identificatore della fermata (può essere {@code null})
     */
    public String getStopId() {
        return stopId;
    }

    /**
     * Imposta l'identificatore della fermata.
     *
     * @param stopId identificatore della fermata
     */
    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    /**
     * Restituisce l'ordine della fermata nella corsa.
     *
     * @return ordine della fermata (può essere {@code null})
     */
    public Integer getStopSequence() {
        return stopSequence;
    }

    /**
     * Imposta l'ordine della fermata nella corsa.
     *
     * @param stopSequence ordine della fermata
     */
    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    /**
     * Restituisce la destinazione mostrata sul veicolo.
     *
     * @return destinazione mostrata (può essere {@code null})
     */
    public String getStopHeadsign() {
        return stopHeadsign;
    }

    /**
     * Imposta la destinazione mostrata sul veicolo.
     *
     * @param stopHeadsign destinazione mostrata
     */
    public void setStopHeadsign(String stopHeadsign) {
        this.stopHeadsign = stopHeadsign;
    }

    /**
     * Restituisce la modalità di salita.
     *
     * @return modalità di salita (può essere {@code null})
     */
    public Integer getPickupType() {
        return pickupType;
    }

    /**
     * Imposta la modalità di salita.
     *
     * @param pickupType modalità di salita
     */
    public void setPickupType(Integer pickupType) {
        this.pickupType = pickupType;
    }

    /**
     * Restituisce la modalità di discesa.
     *
     * @return modalità di discesa (può essere {@code null})
     */
    public Integer getDropOffType() {
        return dropOffType;
    }

    /**
     * Imposta la modalità di discesa.
     *
     * @param dropOffType modalità di discesa
     */
    public void setDropOffType(Integer dropOffType) {
        this.dropOffType = dropOffType;
    }

    /**
     * Restituisce la distanza percorsa lungo la shape.
     *
     * @return distanza percorsa (può essere {@code null})
     */
    public Double getShapeDistTraveled() {
        return shapeDistTraveled;
    }

    /**
     * Imposta la distanza percorsa lungo la shape.
     *
     * @param shapeDistTraveled distanza percorsa
     */
    public void setShapeDistTraveled(Double shapeDistTraveled) {
        this.shapeDistTraveled = shapeDistTraveled;
    }

    /**
     * Restituisce la precisione dell'orario.
     *
     * @return precisione dell'orario (può essere {@code null})
     */
    public Integer getTimepoint() {
        return timepoint;
    }

    /**
     * Imposta la precisione dell'orario.
     *
     * @param timepoint precisione dell'orario
     */
    public void setTimepoint(Integer timepoint) {
        this.timepoint = timepoint;
    }
}


