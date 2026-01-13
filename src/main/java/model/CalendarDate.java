package model;

// Classe per gestione delle date.
import java.time.LocalDate;

/**
 * JavaBean che rappresenta un'eccezione di calendario per un servizio.
 * <p>
 * Contiene le date da rimuovere o aggiungere al calendario di servizio (GTFS {@code calendar_dates.txt}).
 * </p>
 * <ul>
 *     <li>{@code serviceId} - identificatore univoco del servizio</li>
 *     <li>{@code date} - data dell'eccezione</li>
 *     <li>{@code exceptionType} - tipo di eccezione:
 *         <ul>
 *             <li>{@code 1} = servizio attivo</li>
 *             <li>{@code 2} = servizio non attivo</li>
 *         </ul>
 *     </li>
 * </ul>
 */
public class CalendarDate {
    private String serviceId;
    private LocalDate date;
    private Integer exceptionType;

    /**
     * Costruttore vuoto.
     */
    public CalendarDate() {
        // Costruttore vuoto richiesto da Apache Commons CSV, che crea oggetti e poi li riempie con i setter.
    }

    /**
     * Restituisce l'identificatore univoco del servizio.
     *
     * @return identificatore univoco del servizio (può essere {@code null})
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Imposta l'identificatore univoco del servizio.
     *
     * @param serviceId identificatore univoco del servizio
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Restituisce la data dell'eccezione.
     *
     * @return data dell'eccezione (può essere {@code null})
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Imposta la data dell'eccezione.
     *
     * @param date data dell'eccezione
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Restituisce il tipo di eccezione.
     *
     * @return tipo di eccezione (può essere {@code null})
     */
    public Integer getExceptionType() {
        return exceptionType;
    }

    /**
     * Imposta il tipo di eccezione.
     *
     * @param exceptionType tipo di eccezione
     */
    public void setExceptionType(Integer exceptionType) {
        this.exceptionType = exceptionType;
    }
}

