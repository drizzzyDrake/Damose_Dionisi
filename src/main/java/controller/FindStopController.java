package controller;

// Model.
import model.*;

// Operator.
import operator.GTFSDataIndexer;

// Altre classi.
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller per la ricerca e gestione delle fermate.
 */
public class FindStopController {

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private final GTFSDataIndexer dataIndexer;          // GTFS statici indicizzati.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param dataIndexer operator contenente i dati GTFS indicizzati
     */
    public FindStopController(GTFSDataIndexer dataIndexer) {
        this.dataIndexer = dataIndexer;
    }

    // RICERCA LE FERMATE IN BASE ALLA QUERY DELL'UTENTE ---------------------------------------------------------------
    /**
     * Cerca le fermate in base a una query testuale dell'utente.
     * <p>
     * La ricerca controlla se la query corrisponde al nome o all'ID della fermata.
     * Per ogni fermata corrispondente, vengono generate stringhe di suggerimento
     * contenenti il nome e l'ID della fermata.
     * </p>
     *
     * @param query stringa di ricerca dell'utente
     * @return      lista di suggerimenti contenenti nome e ID della fermata
     */
    public List<String> searchStops(String query) {

        List<String> suggestions = new ArrayList<>();                                       // Lista di fermate che vengono visualizzate come suggerimento.

        for (Stop stop : dataIndexer.getAllStops()) {                                       // Per ogni Stop...
            if ((stop.getStopName() != null &&                                              // ...Controlla che il nome sia valido...
                    stop.getStopName().toLowerCase().contains(query.toLowerCase())) ||      // ...Controlla se il nome corrisponde alla query...
                    stop.getStopId().toLowerCase().contains(query.toLowerCase())) {         // ...Controlla se il codice corrisponde alla query.
                String suggestion = stop.getStopName() + " [" + stop.getStopId() + "]";     // Se tutto corrisponde crea la riga di suggerimento...
                suggestions.add(suggestion);                                                // ...E la aggiunge alla lista dei suggerimenti.
            }
        }
        return suggestions;                                                                 // Restituisce la lista di fermate della tendina dei suggerimenti.
    }

    // RESTITUISCE UNA FERMATA IN BASE ALL'ID --------------------------------------------------------------------------
    /**
     * Restituisce una fermata in base al suo ID.
     *
     * @param stopId ID della fermata
     * @return       oggetto {@link Stop}
     */
    public Stop getStopById(String stopId) {
        return dataIndexer.getStopById(stopId);
    }
}



