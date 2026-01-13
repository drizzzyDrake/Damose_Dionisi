package model.repository;

// Model.
import model.Agency;
import model.CalendarDate;
import model.Route;
import model.StopTime;

// Classi per parsing.
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

// Altre classi.
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository per la gestione delle entità {@link StopTime}
 * <p>
 * Questa classe si occupa di leggere i dati degli stopTime da un file CSV e di
 * caricarli in memoria come oggetti {@code StopTime}. I dati vengono memorizzati
 * in una lista interna, accessibile tramite appositi metodi getter.
 * </p>
 * <p>
 * Utilizza Apache Commons CSV per il parsing dei file.
 * </p>
 */
public class StopTimeRepository {

    // Lista che contiene tutti gli oggetti StopTime parsati dal file.
    private List<StopTime> stopTimes = new ArrayList<>();

    /**
     * Carica il file {@code stop_times.txt} e costruisce la lista dei {@link StopTime}.
     * <p>
     * Ogni riga del file viene convertita in un oggetto {@link StopTime},
     * con i valori letti e assegnati ai relativi campi tramite i metodi setter.
     * </p>
     * <p>
     * Utilizza un blocco try-with-resources per garantire la chiusura automatica
     * dello {@link InputStream} e del {@link CSVParser}.
     * </p>
     *
     * @param input lo {@link InputStream} contenente il file {@code stop_times.txt}
     * @throws IOException se si verifica un errore durante la lettura o il parsing del file
     */
    public void loadStopTimesFromStream(InputStream input) throws IOException {     // Eccezione propagata al chiamante (controller).
        try (CSVParser parser = CSVFormat.DEFAULT                                   // try-with-resources : chiude automaticamente InputStream e CSVParser.
                .withFirstRecordAsHeader()                                          // Dice al parser che la prima riga contiene i nomi delle colonne.
                .parse(new InputStreamReader(input))){                              // Carica il file CSV da uno stream specificato.

            for (CSVRecord record : parser) {   // Per ogni riga del file...
                StopTime stopTime = new StopTime();                         // ...Crea un nuovo oggetto StopTime...
                stopTime.setTripId(record.get("trip_id"));                              // ...Imposta ogni campo usando i setter...
                stopTime.setArrivalTime(record.get("arrival_time"));
                stopTime.setDepartureTime(record.get("departure_time"));
                stopTime.setStopId(record.get("stop_id"));
                stopTime.setStopSequence(parseInt(record.get("stop_sequence")));
                stopTime.setStopHeadsign(record.get("stop_headsign"));
                stopTime.setPickupType(parseInt(record.get("pickup_type")));
                stopTime.setDropOffType(parseInt(record.get("drop_off_type")));
                stopTime.setShapeDistTraveled(parseDouble(record.get("shape_dist_traveled")));
                stopTime.setTimepoint(parseInt(record.get("timepoint")));
                stopTimes.add(stopTime);                                 // ...Aggiunge la fermata alla lista.
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
     * Converte in modo sicuro una stringa in un numero decimale, gestendo i casi di campi vuoti o mancanti.
     * <p>
     * Questo metodo evita eccezioni {@link NumberFormatException} restituendo {@code null}
     * se il valore fornito è nullo o vuoto.
     * </p>
     *
     * @param value la stringa da convertire
     * @return il valore double corrispondente o {@code null} se il campo è vuoto o mancante
     */
    private Double parseDouble(String value) {
        return (value == null || value.isEmpty()) ? null : Double.parseDouble(value);
    }

    /**
     * Restituisce tutti gli stoptime caricati dal file CSV.
     *
     * @return lista completa di {@link StopTime}
     */
    public List<StopTime> getAllStopTimes() {
        return stopTimes;
    }
}
