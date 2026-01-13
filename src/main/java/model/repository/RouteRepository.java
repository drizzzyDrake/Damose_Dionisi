package model.repository;

// Model.
import model.Agency;
import model.CalendarDate;
import model.Route;

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
 * Repository per la gestione delle entità {@link Route}
 * <p>
 * Questa classe si occupa di leggere i dati delle linee da un file CSV e di
 * caricarli in memoria come oggetti {@code Route}. I dati vengono memorizzati
 * in una lista interna, accessibile tramite appositi metodi getter.
 * </p>
 * <p>
 * Utilizza Apache Commons CSV per il parsing dei file.
 * </p>
 */
public class RouteRepository {

    // Lista che contiene tutti gli oggetti Route parsati dal file.
    private List<Route> routes = new ArrayList<>();

    /**
     * Carica il file {@code routes.txt} e costruisce la lista dei {@link Route}.
     * <p>
     * Ogni riga del file viene convertita in un oggetto {@link Route},
     * con i valori letti e assegnati ai relativi campi tramite i metodi setter.
     * </p>
     * <p>
     * Utilizza un blocco try-with-resources per garantire la chiusura automatica
     * dello {@link InputStream} e del {@link CSVParser}.
     * </p>
     *
     * @param input lo {@link InputStream} contenente il file {@code routes.txt}
     * @throws IOException se si verifica un errore durante la lettura o il parsing del file
     */
    public void loadRoutesFromStream(InputStream input) throws IOException {        // Eccezione propagata al chiamante (controller).
        try (CSVParser parser = CSVFormat.DEFAULT                                   // try-with-resources : chiude automaticamente InputStream e CSVParser.
                .withFirstRecordAsHeader()                                          // Dice al parser che la prima riga contiene i nomi delle colonne.
                .parse(new InputStreamReader(input))){                              // Carica il file CSV da uno stream specificato.

            for (CSVRecord record : parser) {   // Per ogni riga del file...
                Route route = new Route();                         // ...Crea un nuovo oggetto Route...
                route.setRouteId(record.get("route_id"));          // ...Imposta ogni campo usando i setter...
                route.setAgencyId(record.get("agency_id"));
                route.setRouteShortName(record.get("route_short_name"));
                route.setRouteLongName(record.get("route_long_name"));
                route.setRouteType(parseInt(record.get("route_type")));
                route.setRouteUrl(record.get("route_url"));
                route.setRouteColor(record.get("route_color"));
                route.setRouteTextColor(record.get("route_text_color"));
                routes.add(route);                                 // ...Aggiunge la strada alla lista.
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
     * Restituisce tutte le route caricate dal file CSV.
     *
     * @return lista completa di {@link Route}
     */
    public List<Route> getAllRoutes() {
        return routes;
    }
}

