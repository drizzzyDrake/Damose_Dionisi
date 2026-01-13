package model;

/**
 * JavaBean che rappresenta una fermata o stazione.
 * <p>
 * Contiene tutte le informazioni principali sulla fermata (GTFS {@code stops.txt}).
 * </p>
 * <ul>
 *     <li>{@code stopId} - identificatore univoco</li>
 *     <li>{@code stopCode} - codice identificativo pubblico</li>
 *     <li>{@code stopName} - nome</li>
 *     <li>{@code stopDesc} - descrizione aggiuntiva</li>
 *     <li>{@code stopLat} - latitudine della fermata</li>
 *     <li>{@code stopLon} - longitudine della fermata</li>
 *     <li>{@code stopUrl} - eventuale URL informativo</li>
 *     <li>{@code wheelchairBoarding} - accessibilità sedia a rotelle (0 = sconosciuto, 1 = accessibile, 2 = non accessibile)</li>
 *     <li>{@code stopTimezone} - fuso orario (es. "Europe/Rome")</li>
 *     <li>{@code locationType} - tipo di fermata (0 = fermata fisica, 1 = stazione, 2–4 = altri tipi)</li>
 *     <li>{@code parentStation} - identificatore della stazione di appartenenza</li>
 * </ul>
 */
public class Stop {
    private String stopId;
    private String stopCode;
    private String stopName;
    private String stopDesc;
    private Double stopLat;
    private Double stopLon;
    private String stopUrl;
    private Integer wheelchairBoarding;
    private String stopTimezone;
    private Integer locationType;
    private String parentStation;

    /**
     * Costruttore vuoto.
     */
    public Stop() {
        // Costruttore vuoto richiesto da Apache Commons CSV, che crea oggetti e poi li riempie con i setter.
    }

    /**
     * Restituisce l'identificatore univoco della fermata.
     *
     * @return identificatore univoco della fermata (può essere {@code null})
     */
    public String getStopId() {
        return stopId;
    }

    /**
     * Imposta l'identificatore univoco della fermata.
     *
     * @param stopId identificatore univoco della fermata
     */
    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    /**
     * Restituisce il codice pubblico della fermata.
     *
     * @return codice pubblico della fermata (può essere {@code null})
     */
    public String getStopCode() {
        return stopCode;
    }

    /**
     * Imposta il codice pubblico della fermata.
     *
     * @param stopCode codice pubblico della fermata
     */
    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    /**
     * Restituisce il nome della fermata.
     *
     * @return nome della fermata (può essere {@code null})
     */
    public String getStopName() {
        return stopName;
    }

    /**
     * Imposta il nome della fermata.
     *
     * @param stopName nome della fermata
     */
    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    /**
     * Restituisce la descrizione della fermata.
     *
     * @return descrizione della fermata (può essere {@code null})
     */
    public String getStopDesc() {
        return stopDesc;
    }

    /**
     * Imposta la descrizione della fermata.
     *
     * @param stopDesc descrizione della fermata
     */
    public void setStopDesc(String stopDesc) {
        this.stopDesc = stopDesc;
    }

    /**
     * Restituisce la latitudine della fermata.
     *
     * @return latitudine (può essere {@code null})
     */
    public Double getStopLat() {
        return stopLat;
    }

    /**
     * Imposta la latitudine della fermata.
     *
     * @param stopLat latitudine
     */
    public void setStopLat(Double stopLat) {
        this.stopLat = stopLat;
    }

    /**
     * Restituisce la longitudine della fermata.
     *
     * @return longitudine (può essere {@code null})
     */
    public Double getStopLon() {
        return stopLon;
    }

    /**
     * Imposta la longitudine della fermata.
     *
     * @param stopLon longitudine
     */
    public void setStopLon(Double stopLon) {
        this.stopLon = stopLon;
    }

    /**
     * Restituisce l'URL informativo della fermata.
     *
     * @return URL informativo (può essere {@code null})
     */
    public String getStopUrl() {
        return stopUrl;
    }

    /**
     * Imposta l'URL informativo della fermata.
     *
     * @param stopUrl URL informativo della fermata
     */
    public void setStopUrl(String stopUrl) {
        this.stopUrl = stopUrl;
    }

    /**
     * Restituisce il codice di accessibilità.
     *
     * @return codice di accessibilità (può essere {@code null})
     */
    public Integer getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    /**
     * Imposta il codice di accessibilità.
     *
     * @param wheelchairBoarding codice di accessibilità
     */
    public void setWheelchairBoarding(Integer wheelchairBoarding) {
        this.wheelchairBoarding = wheelchairBoarding;
    }

    /**
     * Restituisce il fuso orario della fermata.
     *
     * @return fuso orario (può essere {@code null})
     */
    public String getStopTimezone() {
        return stopTimezone;
    }

    /**
     * Imposta il fuso orario della fermata.
     *
     * @param stopTimezone fuso orario
     */
    public void setStopTimezone(String stopTimezone) {
        this.stopTimezone = stopTimezone;
    }

    /**
     * Restituisce il tipo di fermata.
     *
     * @return tipo di fermata (può essere {@code null})
     */
    public Integer getLocationType() {
        return locationType;
    }

    /**
     * Imposta il tipo di fermata.
     *
     * @param locationType tipo di fermata
     */
    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    /**
     * Restituisce la stazione padre.
     *
     * @return stazione padre (può essere {@code null})
     */
    public String getParentStation() {
        return parentStation;
    }

    /**
     * Imposta la stazione padre.
     *
     * @param parentStation stazione padre
     */
    public void setParentStation(String parentStation) {
        this.parentStation = parentStation;
    }
}

