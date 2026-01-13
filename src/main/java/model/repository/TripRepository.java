package model.repository;

// Model.
import model.Agency;
import model.CalendarDate;
import model.Route;
import model.Trip;

// Classi per parsing.
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

// Altre classi.
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Repository per la gestione delle entità {@link Trip}
 * <p>
 * Questa classe si occupa di leggere i dati delle corse da un file CSV e di
 * caricarli in memoria come oggetti {@code Trip}. I dati vengono memorizzati
 * in una lista interna, accessibile tramite appositi metodi getter.
 * </p>
 * <p>
 * Utilizza Apache Commons CSV per il parsing dei file.
 * </p>
 */
public class TripRepository {

    // Lista che contiene tutti gli oggetti Trip parsati dal file.
    private List<Trip> trips = new ArrayList<>();

    /**
     * Carica il file {@code trips.txt} e costruisce la lista dei {@link Trip}.
     * <p>
     * Ogni riga del file viene convertita in un oggetto {@link Trip},
     * con i valori letti e assegnati ai relativi campi tramite i metodi setter.
     * </p>
     * <p>
     * Utilizza un blocco try-with-resources per garantire la chiusura automatica
     * dello {@link InputStream} e del {@link CSVParser}.
     * </p>
     *
     * @param input lo {@link InputStream} contenente il file {@code trips.txt}
     * @throws IOException se si verifica un errore durante la lettura o il parsing del file
     */
    public void loadTripsFromStream(InputStream input) throws IOException {     // Eccezione propagata al chiamante (controller).
        try (CSVParser parser = CSVFormat.DEFAULT                               // try-with-resources : chiude automaticamente InputStream e CSVParser.
                .withFirstRecordAsHeader()                                      // Dice al parser che la prima riga contiene i nomi delle colonne.
                .parse(new InputStreamReader(input))){                          // Carica il file CSV da uno stream specificato.

            for (CSVRecord record : parser) {   // Per ogni riga del file...
                Trip trip = new Trip();                             // ...Crea un nuovo oggetto Trip...
                trip.setRouteId(record.get("route_id"));            // ...Imposta ogni campo usando i setter...
                trip.setServiceId(record.get("service_id"));
                trip.setTripId(record.get("trip_id"));
                trip.setTripHeadsign(record.get("trip_headsign"));
                trip.setTripShortName(record.get("trip_short_name"));
                trip.setDirectionId(parseInt(record.get("direction_id")));
                trip.setBlockId(record.get("block_id"));
                trip.setShapeId(record.get("shape_id"));
                trip.setWheelchairAccessible(parseInt(record.get("wheelchair_accessible")));
                trip.setExceptional(parseInt(record.get("exceptional")));
                trips.add(trip);                                 // ...Aggiunge il viaggio alla lista.
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
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : Integer.parseInt(trimmed);
    }

    /**
     * Restituisce tutte le corse caricate dal file CSV.
     *
     * @return lista completa di {@link Trip}
     */
    public List<Trip> getAllTrips() {
        return trips;
    }
}


