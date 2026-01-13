package model.repository;

// Model.
import model.CalendarDate;

// Classi per parsing.
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

// Altre classi.
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Repository per la gestione delle entità {@link CalendarDate}.
 * <p>
 * Questa classe si occupa di leggere i dati del calendario di servizio da un file CSV e di
 * caricarli in memoria come oggetti {@code CalendarDate}. I dati vengono memorizzati
 * in un insieme interno, accessibile tramite appositi metodi getter.
 * </p>
 * <p>
 * Utilizza Apache Commons CSV per il parsing dei file.
 * </p>
 */
public class CalendarDateRepository {

    // Lista che contiene tutti gli oggetti CalendarDate parsati dal file.
    private List<CalendarDate> calendarDates = new ArrayList<>();

    // Mappa che associa ogni data a un insieme di servizi attivi in quel giorno.
    private Map<LocalDate, Set<String>> activeServicesByDate = new HashMap<>();

    // Formattatore per convertire le date dal formato stringa "yyyyMMdd" in LocalDate.
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Carica il file {@code calendar_dates.txt} e costruisce l'insieme dei {@link CalendarDate} attivi.
     * <p>
     * Ogni riga del file viene convertita in un oggetto {@link CalendarDate},
     * con i valori letti e assegnati ai relativi campi tramite i metodi setter.
     * </p>
     * <p>
     * Utilizza un blocco try-with-resources per garantire la chiusura automatica
     * dello {@link InputStream} e del {@link CSVParser}.
     * </p>
     *
     * @param input lo {@link InputStream} contenente il file {@code calendar_dates.txt}
     * @throws IOException se si verifica un errore durante la lettura o il parsing del file
     */
    public void loadCalendarDatesFromStream(InputStream input) throws IOException {     // Eccezione propagata al chiamante (controller).
        try (CSVParser parser = CSVFormat.DEFAULT                                       // try-with-resources : chiude automaticamente InputStream e CSVParser.
                .withFirstRecordAsHeader()                                              // Dice al parser che la prima riga contiene i nomi delle colonne.
                .parse(new InputStreamReader(input))){                                  // Carica il file CSV da uno stream specificato.

            // Estrae i valori dalla riga e li converte nei tipi corretti (String, LocalDate, Integer).
            for (CSVRecord record : parser) {
                String serviceId = record.get("service_id");
                LocalDate date = parseDate(record.get("date"));
                Integer exceptionType = parseInt(record.get("exception_type"));

                // Salta la riga se uno dei campi essenziali è mancante.
                if (serviceId == null || date == null || exceptionType == null) continue;

                CalendarDate cd = new CalendarDate();   // Creazione dell'oggetto CalendarDate.
                cd.setServiceId(serviceId);
                cd.setDate(date);
                cd.setExceptionType(exceptionType);
                calendarDates.add(cd);                  // Aggiunta dell'oggetto alla lista.

                // Se il tipo di eccezione è 1 (aggiunta di servizio), aggiunge il serviceId alla mappa per quella data.
                if (exceptionType == 1) {
                    activeServicesByDate
                            .computeIfAbsent(date, d -> new HashSet<>())
                            .add(serviceId);
                }
            }
        }
    }

    /**
     * Converte in modo sicuro una stringa in un intero, gestendo i casi di campi vuoti o mancanti.
     * <p>
     * Questo metodo evita eccezioni {@link NumberFormatException} restituendo {@code null}
     * se il valore fornito è nullo o vuoto.
     * </p>
     *
     * @param value la stringa da convertire
     * @return il valore intero corrispondente o {@code null} se il campo è vuoto o mancante
     */
    private Integer parseInt(String value) {
        return (value == null || value.isEmpty()) ? null : Integer.parseInt(value);
    }

    /**
     * Esegue un parsing sicuro di una data in formato {@code yyyyMMdd}.
     * <p>
     * Questo metodo evita eccezioni {@link NumberFormatException} restituendo {@code null}
     * se il valore fornito è nullo o vuoto.
     * </p>
     *
     * @param value la stringa da convertire in {@link LocalDate}
     * @return la data corrispondente o {@code null} se il valore è vuoto o nullo
     */
    private LocalDate parseDate(String value) {
        return (value == null || value.isEmpty()) ? null : LocalDate.parse(value, formatter);
    }

    /**
     * Restituisce l'insieme dei {@code service_id} attivi in una specifica data.
     *
     * @param date la data da verificare
     * @return insieme di {@link CalendarDate} attivi per data
     */
    public Set<String> getActiveServicesOnDate(LocalDate date) {
        return activeServicesByDate.getOrDefault(date, Collections.emptySet());
    }

    /**
     * Restituisce tutte le date in cui un determinato {@code service_id} è attivo.
     *
     * @param serviceId l'identificatore del servizio
     * @return lista di {@link CalendarDate} in cui il servizio è attivo
     */
    public List<LocalDate> getDatesForService(String serviceId) {
        List<LocalDate> dates = new ArrayList<>();
        for (CalendarDate cd : calendarDates) {
            if (cd.getServiceId().equals(serviceId) && cd.getExceptionType() == 1) {
                dates.add(cd.getDate());
            }
        }
        return dates;
    }

    /**
     * Restituisce tutte le date presenti nel file CSV
     * in cui è attivo almeno un servizio (solo quelle con {@code exception_type = 1}).
     *
     * @return insieme di {@link CalendarDate} con almeno un servizio attivo
     */
    public Set<LocalDate> getAllDates() {
        return activeServicesByDate.keySet();
    }
}
