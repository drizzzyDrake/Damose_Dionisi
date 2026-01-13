package model;

import java.util.Objects;

/**
 * JavaBean che rappresenta un arrivo previsto o realtime di una corsa.
 * <p>
 * Contiene tutte le informazioni principali sugli arrivi alle fermate (derivati da GTFS statico e realtime).
 * </p>
 * <ul>
 *     <li>{@code routeId} - identificatore della linea</li>
 *     <li>{@code tripHeadsign} - destinazione o intestazione della corsa</li>
 *     <li>{@code formattedTime} - orario formattato di arrivo (es. "14:35")</li>
 *     <li>{@code realtime} - indica se l'orario è realtime (true) o statico (false)</li>
 *     <li>{@code status} - stato della fermata rispetto alla corsa ({@link Arrival.StopStatus} NEXT, FUTURE, PAST)</li>
 *     <li>{@code delayMinutes} - ritardo o anticipo in minuti (può essere {@code null})</li>
 *     <li>{@code stopId} - identificatore della fermata</li>
 *     <li>{@code tripId} - identificatore della corsa</li>
 * </ul>
 */
public class Arrival {

    /**
     * Stato di una fermata rispetto al percorso corrente.
     * <ul>
     *      <li>NEXT - la prossima fermata da raggiungere</li>
     *      <li>FUTURE - fermata futura, non ancora raggiunta</li>
     *      <li>PAST - fermata già superata</li>
     * </ul>
     */
    public enum StopStatus { NEXT, FUTURE, PAST }

    private final String routeId;
    private final String tripHeadsign;
    private final String formattedTime;
    private final boolean realtime;
    private final StopStatus status;
    private final Long delayMinutes;
    private final String stopId;
    private final String tripId;

    /**
     * Costruttore.
     *
     * @param routeId       identificatore della linea
     * @param tripHeadsign  destinazione/intestazione della corsa
     * @param formattedTime orario formattato di arrivo
     * @param realtime      true se l'orario è realtime, false se statico
     * @param status        stato della fermata rispetto alla corsa
     * @param delayMinutes  ritardo/anticipo in minuti (può essere {@code null})
     * @param stopId        identificatore della fermata
     * @param tripId        identificatore della corsa
     */
    public Arrival(String routeId,
                   String tripHeadsign,
                   String formattedTime,
                   boolean realtime,
                   StopStatus status,
                   Long delayMinutes,
                   String stopId,
                   String tripId) {
        this.routeId = routeId;
        this.tripHeadsign = tripHeadsign;
        this.formattedTime = formattedTime;
        this.realtime = realtime;
        this.status = status;
        this.delayMinutes = delayMinutes;
        this.stopId = stopId;
        this.tripId = tripId;
    }

    /**
     * Restituisce l'identificatore della linea.
     *
     * @return identificatore della linea (può essere {@code null})
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * Restituisce la destinazione/intestazione della corsa.
     *
     * @return destinazione/intestazione della corsa (può essere {@code null})
     */
    public String getTripHeadsign() {
        return tripHeadsign;
    }

    /**
     * Restituisce l'orario formattato di arrivo.
     *
     * @return orario formattato di arrivo
     */
    public String getFormattedTime() {
        return formattedTime;
    }

    /**
     * Indica se l'orario è realtime.
     *
     * @return {@code true} se l'orario è realtime, {@code false} se statico
     */
    public boolean isRealtime() {
        return realtime;
    }

    /**
     * Restituisce lo stato della fermata rispetto alla corsa.
     *
     * @return stato della fermata ({@link Arrival.StopStatus})
     */
    public StopStatus getStatus() {
        return status;
    }

    /**
     * Restituisce il ritardo o anticipo in minuti.
     *
     * @return ritardo/anticipo in minuti (può essere {@code null})
     */
    public Long getDelayMinutes() {
        return delayMinutes;
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
     * Restituisce l'identificatore della corsa.
     *
     * @return identificatore della corsa (può essere {@code null})
     */
    public String getTripId() {
        return tripId;
    }

    /**
     * Restituisce una rappresentazione testuale dell'arrivo.
     *
     * @return stringa rappresentativa dell'arrivo
     */
    @Override
    public String toString() {
        return routeId + " → " + tripHeadsign + " (" + formattedTime + ")";
    }

    /**
     * Ridefinisce l'uguaglianza tra oggetti Arrival.
     * Due Arrival sono considerati uguali se hanno lo stesso routeId
     * e lo stesso orario formattato.
     *
     * @param  o oggetto da confrontare
     * @return true se uguali, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arrival)) return false;
        Arrival other = (Arrival) o;
        return Objects.equals(routeId, other.routeId)
                && Objects.equals(formattedTime, other.formattedTime);
    }

    /**
     * Calcola l'hashcode dell'oggetto Arrival
     * basato su routeId e orario formattato.
     *
     * @return valore hash dell'oggetto
     */
    @Override
    public int hashCode() {
        return Objects.hash(routeId, formattedTime);
    }
}
