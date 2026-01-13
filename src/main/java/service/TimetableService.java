package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Classe service per la gestione degli orari GTFS.
 * <p>
 * Si occupa della gestione delle stringhe di orario contenute nei dati GTFS.
 * </p>
 */
public class TimetableService {

    // CONVERTE GLI ORARI STOPTIMES IN OGGETTI LOCALDATETIME -----------------------------------------------------------
    /**
     * Converte un orario in formato HH:mm:ss o HH:mm in {@link LocalDateTime},
     * gestendo anche orari superiori a 24 (giorno successivo).
     *
     * @param time stringa orario
     * @return     oggetto {@link LocalDateTime}
     */
    public static LocalDateTime parseArrivalTimeSafe(String time) {
        try {
            String[] parts = time.split(":");                                     // parts = ["HH", "mm", "ss"].
            int h = Integer.parseInt(parts[0]);                                         // Converte "HH" in un intero e trova le ore.
            int m = Integer.parseInt(parts[1]);                                         // Converte "mm" in un intero e trova i minuti.
            int s = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;                  // Converte "ss" in un intero e trova i secondi.

            LocalDate day = LocalDate.now();                                            // Data corrente.
            if (h >= 24) {                                                              // Nei dati GTFS potrebbero esserci orari che superano le 24 (giorno successivo)...
                h -= 24;                                                                // ...Ad esempio 25, corrisponde a: 25-24 = 1.
                day = day.plusDays(1);                                        // L'orario si riferisce al giorno successivo.
            }
            return LocalDateTime.of(day, LocalTime.of(h, m, s));                        // Data e orario di arrivo (secondi in realtà trascurabili).
        } catch (Exception e) {
            return null;
        }
    }
}
