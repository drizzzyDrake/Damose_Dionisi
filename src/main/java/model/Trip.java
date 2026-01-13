package model;

/**
 * JavaBean che rappresenta una corsa.
 * <p>
 * Contiene tutte le informazioni principali sulla corsa (GTFS {@code trips.txt}).
 * </p>
 * <ul>
 *     <li>{@code routeId} - linea di appartenenza</li>
 *     <li>{@code serviceId} - servizio associato</li>
 *     <li>{@code tripId} - identificatore univoco</li>
 *     <li>{@code tripHeadsign} - destinazione mostrata sul veicolo</li>
 *     <li>{@code tripShortName} - nome breve</li>
 *     <li>{@code directionId} - direzione del viaggio (0 = andata, 1 = ritorno)</li>
 *     <li>{@code blockId} - identifica un gruppo di corse consecutive effettuate dallo stesso veicolo</li>
 *     <li>{@code shapeId} - identifica la shape (percorso geografico) della corsa</li>
 *     <li>{@code wheelchairAccessible} - accessibilità sedia a rotelle (0 = sconosciuto, 1 = accessibile, 2 = non accessibile)</li>
 *     <li>{@code exceptional} - corse eccezionali</li>
 * </ul>
 */
public class Trip {
    private String routeId;
    private String serviceId;
    private String tripId;
    private String tripHeadsign;
    private String tripShortName;
    private Integer directionId;
    private String blockId;
    private String shapeId;
    private Integer wheelchairAccessible;
    private Integer exceptional;

    /**
     * Costruttore vuoto.
     */
    public Trip() {
        // Costruttore vuoto richiesto da Apache Commons CSV, che crea oggetti e poi li riempie con i setter.
    }

    /**
     * Restituisce la linea di appartenenza.
     *
     * @return linea di appartenenza (può essere {@code null})
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * Imposta la linea di appartenenza.
     *
     * @param routeId linea di appartenenza
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    /**
     * Restituisce il servizio associato.
     *
     * @return servizio associato (può essere {@code null})
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Imposta il servizio associato.
     *
     * @param serviceId servizio associato
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Restituisce l'identificatore univoco.
     *
     * @return identificatore univoco (può essere {@code null})
     */
    public String getTripId() {
        return tripId;
    }

    /**
     * Imposta l'identificatore univoco.
     *
     * @param tripId identificatore univoco
     */
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    /**
     * Restituisce la destinazione mostrata sul veicolo.
     *
     * @return destinazione mostrata (può essere {@code null})
     */
    public String getTripHeadsign() {
        return tripHeadsign;
    }

    /**
     * Imposta la destinazione mostrata sul veicolo.
     *
     * @param tripHeadsign destinazione mostrata
     */
    public void setTripHeadsign(String tripHeadsign) {
        this.tripHeadsign = tripHeadsign;
    }

    /**
     * Restituisce il nome breve.
     *
     * @return nome breve (può essere {@code null})
     */
    public String getTripShortName() {
        return tripShortName;
    }

    /**
     * Imposta il nome breve.
     *
     * @param tripShortName nome breve
     */
    public void setTripShortName(String tripShortName) {
        this.tripShortName = tripShortName;
    }

    /**
     * Restituisce la direzione del viaggio.
     *
     * @return direzione del viaggio (può essere {@code null})
     */
    public Integer getDirectionId() {
        return directionId;
    }

    /**
     * Imposta la direzione del viaggio.
     *
     * @param directionId direzione del viaggio
     */
    public void setDirectionId(Integer directionId) {
        this.directionId = directionId;
    }

    /**
     * Restituisce il blocco di corse consecutive.
     *
     * @return blocco di corse consecutive (può essere {@code null})
     */
    public String getBlockId() {
        return blockId;
    }

    /**
     * Imposta il blocco di corse consecutive.
     *
     * @param blockId blocco di corse consecutive
     */
    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    /**
     * Restituisce la shape geografica associata.
     *
     * @return shape geografica associata (può essere {@code null})
     */
    public String getShapeId() {
        return shapeId;
    }

    /**
     * Imposta la shape geografica associata.
     *
     * @param shapeId shape geografica associata
     */
    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    /**
     * Restituisce il codice di accessibilità.
     *
     * @return codice di accessibilità (può essere {@code null})
     */
    public Integer getWheelchairAccessible() {
        return wheelchairAccessible;
    }

    /**
     * Imposta il codice di accessibilità.
     *
     * @param wheelchairAccessible codice di accessibilità
     */
    public void setWheelchairAccessible(Integer wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    /**
     * Restituisce le corse eccezionali.
     *
     * @return corse eccezionali (può essere {@code null})
     */
    public Integer getExceptional() {
        return exceptional;
    }

    /**
     * Imposta le corse eccezionali.
     *
     * @param exceptional corse eccezionali
     */
    public void setExceptional(Integer exceptional) {
        this.exceptional = exceptional;
    }
}
