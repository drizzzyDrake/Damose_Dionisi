package model;

/**
 * JavaBean che rappresenta una linea di trasporto pubblico.
 * <p>
 * Contiene tutte le informazioni principali sulla linea (GTFS {@code routes.txt}).
 * </p>
 * <ul>
 *     <li>{@code routeId} – identificatore univoco</li>
 *     <li>{@code agencyId} – agenzia responsabile</li>
 *     <li>{@code routeShortName} – nome breve (es. “64”)</li>
 *     <li>{@code routeLongName} – nome completo</li>
 *     <li>{@code routeType} – tipo di mezzo (0 = tram, 1 = metro, 3 = bus)</li>
 *     <li>{@code routeUrl} – eventuale URL informativo</li>
 *     <li>{@code routeColor} – colore della linea in formato esadecimale</li>
 *     <li>{@code routeTextColor} – colore del testo in formato esadecimale</li>
 * </ul>
 */
public class Route {
    private String routeId;
    private String agencyId;
    private String routeShortName;
    private String routeLongName;
    private Integer routeType;
    private String routeUrl;
    private String routeColor;
    private String routeTextColor;

    /**
     * Costruttore vuoto.
     */
    public Route() {
        // Costruttore vuoto richiesto da Apache Commons CSV, che crea oggetti e poi li riempie con i setter.
    }

    /**
     * Restituisce l'identificatore univoco.
     *
     * @return identificatore univoco (può essere {@code null})
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * Imposta l'identificatore univoco.
     *
     * @param routeId identificatore univoco
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    /**
     * Restituisce l'agenzia responsabile.
     *
     * @return agenzia responsabile (può essere {@code null})
     */
    public String getAgencyId() {
        return agencyId;
    }

    /**
     * Imposta l'agenzia responsabile.
     *
     * @param agencyId agenzia responsabile
     */
    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    /**
     * Restituisce il nome breve.
     *
     * @return nome breve (può essere {@code null})
     */
    public String getRouteShortName() {
        return routeShortName;
    }

    /**
     * Imposta il nome breve.
     *
     * @param routeShortName nome breve
     */
    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    /**
     * Restituisce il nome completo.
     *
     * @return nome completo (può essere {@code null})
     */
    public String getRouteLongName() {
        return routeLongName;
    }

    /**
     * Imposta il nome completo.
     *
     * @param routeLongName nome completo
     */
    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    /**
     * Restituisce il tipo di mezzo.
     *
     * @return tipo di mezzo (può essere {@code null})
     */
    public Integer getRouteType() {
        return routeType;
    }

    /**
     * Imposta il tipo di mezzo.
     *
     * @param routeType tipo di mezzo
     */
    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    /**
     * Restituisce l'URL informativo.
     *
     * @return URL informativo (può essere {@code null})
     */
    public String getRouteUrl() {
        return routeUrl;
    }

    /**
     * Imposta l'URL informativo.
     *
     * @param routeUrl URL informativo
     */
    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }

    /**
     * Restituisce il colore della linea.
     *
     * @return colore della linea (può essere {@code null})
     */
    public String getRouteColor() {
        return routeColor;
    }

    /**
     * Imposta il colore della linea.
     *
     * @param routeColor colore della linea
     */
    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }

    /**
     * Restituisce il colore del testo.
     *
     * @return colore del testo (può essere {@code null})
     */
    public String getRouteTextColor() {
        return routeTextColor;
    }

    /**
     * Imposta il colore del testo.
     *
     * @param routeTextColor colore del testo
     */
    public void setRouteTextColor(String routeTextColor) {
        this.routeTextColor = routeTextColor;
    }
}

