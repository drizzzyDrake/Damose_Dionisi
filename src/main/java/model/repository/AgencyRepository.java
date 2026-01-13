package model.repository;

// Model.
import model.Agency;

// Classi per parsing.
import model.CalendarDate;
import model.Route;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

// Altre classi.
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Repository per la gestione delle entità {@link Agency}.
 * <p>
 * Questa classe si occupa di leggere i dati delle agenzie da un file CSV e di
 * caricarli in memoria come oggetti {@code Agency}. I dati vengono memorizzati
 * in una lista interna, accessibile tramite appositi metodi getter.
 * </p>
 * <p>
 * Utilizza Apache Commons CSV per il parsing dei file.
 * </p>
 */
public class AgencyRepository {

    // Lista che contiene tutti gli oggetti Agency parsati dal file.
    private List<Agency> agencies = new ArrayList<>();

    /**
     * Carica il file {@code agency.txt} e costruisce la lista dei {@link Agency}.
     * <p>
     * Ogni riga del file viene convertita in un oggetto {@link Agency},
     * con i valori letti e assegnati ai relativi campi tramite i metodi setter.
     * </p>
     * <p>
     * Utilizza un blocco try-with-resources per garantire la chiusura automatica
     * dello {@link InputStream} e del {@link CSVParser}.
     * </p>
     *
     * @param input lo {@link InputStream} contenente il file {@code agency.txt}
     * @throws IOException se si verifica un errore durante la lettura o il parsing del file
     */
    public void loadAgenciesFromStream(InputStream input) throws IOException {      // Eccezione propagata al chiamante (controller).
        try (CSVParser parser = CSVFormat.DEFAULT                                   // try-with-resources : chiude automaticamente InputStream e CSVParser.
                .withFirstRecordAsHeader()                                          // Dice al parser che la prima riga contiene i nomi delle colonne.
                .parse(new InputStreamReader(input))){                              // Carica il file CSV da uno stream specificato.

            for (CSVRecord record : parser) {   // Per ogni riga del file...
                Agency agency = new Agency();                       // ...Crea un nuovo oggetto Agency...
                agency.setAgencyId(record.get("agency_id"));        // ...Imposta ogni campo usando i setter...
                agency.setAgencyName(record.get("agency_name"));
                agency.setAgencyUrl(record.get("agency_url"));
                agency.setAgencyTimezone(record.get("agency_timezone"));
                agency.setAgencyLang(record.get("agency_lang"));
                agency.setAgencyPhone(record.get("agency_phone"));
                agency.setAgencyFareUrl(record.get("agency_fare_url"));
                agencies.add(agency);                                // ...Aggiunge l'agenzia alla lista.
            }
        }
    }

    /**
     * Restituisce tutte le agenzie caricate dal file CSV.
     *
     * @return lista di {@link Agency}
     */
    public List<Agency> getAllAgencies() {
        return agencies;
    }
}

