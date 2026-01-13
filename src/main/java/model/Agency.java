package model;

/**
 * JavaBean che rappresenta un'agenzia di trasporto.
 * <p>
 * Contiene tutte le informazioni principali sull'agenzia (GTFS {@code agency.txt}).
 * </p>
 * <ul>
 *     <li>{@code agency_id} - identificatore univoco</li>
 *     <li>{@code agency_name} - nome</li>
 *     <li>{@code agency_url} - eventuale URL informativo</li>
 *     <li>{@code agency_timezone} - fuso orario (es. "Europe/Rome")</li>
 *     <li>{@code agency_lang} - lingua principale (es. "it")</li>
 *     <li>{@code agency_phone} - numero di telefono</li>
 *     <li>{@code agency_fare_url} - URL con informazioni sulle tariffe</li>
 * </ul>
 */
public class Agency {
    private String agencyId;
    private String agencyName;
    private String agencyUrl;
    private String agencyTimezone;
    private String agencyLang;
    private String agencyPhone;
    private String agencyFareUrl;

    /**
     * Costruttore vuoto.
     */
    public Agency() {
        // Costruttore vuoto richiesto da Apache Commons CSV, che crea oggetti e poi li riempie con i setter.
    }

    /**
     * Restituisce l'identificatore univoco.
     *
     * @return identificatore univoco (può essere {@code null})
     */
    public String getAgencyId() {
        return agencyId;
    }

    /**
     * Imposta l'identificatore univoco.
     *
     * @param agencyId identificatore univoco
     */
    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    /**
     * Restituisce il nome.
     *
     * @return nome (può essere {@code null})
     */
    public String getAgencyName() {
        return agencyName;
    }

    /**
     * Imposta il nome.
     *
     * @param agencyName nome
     */
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    /**
     * Restituisce l'URL informativo.
     *
     * @return URL informativo (può essere {@code null})
     */
    public String getAgencyUrl() {
        return agencyUrl;
    }

    /**
     * Imposta l'URL informativo.
     *
     * @param agencyUrl URL informativo
     */
    public void setAgencyUrl(String agencyUrl) {
        this.agencyUrl = agencyUrl;
    }

    /**
     * Restituisce il fuso orario.
     *
     * @return fuso orario (può essere {@code null})
     */
    public String getAgencyTimezone() {
        return agencyTimezone;
    }

    /**
     * Imposta il fuso orario.
     *
     * @param agencyTimezone fuso orario
     */
    public void setAgencyTimezone(String agencyTimezone) {
        this.agencyTimezone = agencyTimezone;
    }

    /**
     * Restituisce la lingua principale.
     *
     * @return lingua principale (può essere {@code null})
     */
    public String getAgencyLang() {
        return agencyLang;
    }

    /**
     * Imposta la lingua principale.
     *
     * @param agencyLang lingua principale
     */
    public void setAgencyLang(String agencyLang) {
        this.agencyLang = agencyLang;
    }

    /**
     * Restituisce il numero di telefono.
     *
     * @return numero di telefono (può essere {@code null})
     */
    public String getAgencyPhone() {
        return agencyPhone;
    }

    /**
     * Imposta il numero di telefono.
     *
     * @param agencyPhone numero di telefono
     */
    public void setAgencyPhone(String agencyPhone) {
        this.agencyPhone = agencyPhone;
    }

    /**
     * Restituisce l'URL con informazioni sulle tariffe.
     *
     * @return URL con informazioni sulle tariffe (può essere {@code null})
     */
    public String getAgencyFareUrl() {
        return agencyFareUrl;
    }

    /**
     * Imposta l'URL con informazioni sulle tariffe.
     *
     * @param agencyFareUrl URL con informazioni sulle tariffe
     */
    public void setAgencyFareUrl(String agencyFareUrl) {
        this.agencyFareUrl = agencyFareUrl;
    }
}

