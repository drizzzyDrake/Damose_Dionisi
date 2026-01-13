package model.repository;

// Model.
import model.Agency;
import model.CalendarDate;
import model.Route;
import model.Stop;

// Classi per parsing.
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

// Altre classi
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Repository per la gestione delle entità {@link Stop}
 * <p>
 * Questa classe si occupa di leggere i dati delle fermate da un file CSV e di
 * caricarli in memoria come oggetti {@code Stop}. I dati vengono memorizzati
 * in una lista interna, accessibile tramite appositi metodi getter.
 * </p>
 * <p>
 * Utilizza Apache Commons CSV per il parsing dei file.
 * </p>
 */
public class StopRepository {

    // Lista che contiene tutti gli oggetti Stop parsati dal file.
    private List<Stop> stops = new ArrayList<>();

    /**
     * Carica il file {@code stops.txt} e costruisce la lista dei {@link Stop}.
     * <p>
     * Ogni riga del file viene convertita in un oggetto {@link Stop},
     * con i valori letti e assegnati ai relativi campi tramite i metodi setter.
     * </p>
     * <p>
     * Utilizza un blocco try-with-resources per garantire la chiusura automatica
     * dello {@link InputStream} e del {@link CSVParser}.
     * </p>
     *
     * @param input lo {@link InputStream} contenente il file {@code stops.txt}
     * @throws IOException se si verifica un errore durante la lettura o il parsing del file
     */
    public void loadStopsFromStream(InputStream input) throws IOException {     // Eccezione propagata al chiamante (controller).
        try (CSVParser parser = CSVFormat.DEFAULT                               // try-with-resources : chiude automaticamente InputStream e CSVParser.
                .withFirstRecordAsHeader()                                      // Dice al parser che la prima riga contiene i nomi delle colonne.
                .parse(new InputStreamReader(input))){                          // Carica il file CSV da uno stream specificato.

            for (CSVRecord record : parser) {   // Per ogni riga del file...
                Stop stop = new Stop();                         // ...Crea un nuovo oggetto Stop...
                stop.setStopId(record.get("stop_id"));          // ...Imposta ogni campo usando i setter...
                stop.setStopCode(record.get("stop_code"));
                stop.setStopName(record.get("stop_name"));
                stop.setStopDesc(record.get("stop_desc"));
                stop.setStopLat(parseDouble(record.get("stop_lat")));
                stop.setStopLon(parseDouble(record.get("stop_lon")));
                stop.setStopUrl(record.get("stop_url"));
                stop.setWheelchairBoarding(parseInt(record.get("wheelchair_boarding")));
                stop.setStopTimezone(record.get("stop_timezone"));
                stop.setLocationType(parseInt(record.get("location_type")));
                stop.setParentStation(record.get("parent_station"));
                stops.add(stop);                                 // ...Aggiunge la fermata alla lista.
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
     * Restituisce tutte le fermate caricate dal file CSV.
     *
     * @return lista completa di {@link Stop}
     */
    public List<Stop> getAllStops() {
        return stops;
    }
}


