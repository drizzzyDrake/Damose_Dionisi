package service;

// Model.
import model.Trip;

// Classi utili.
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

/**
 * Classe service per la gestione dei preferiti di utenti.
 * <p>
 * Permette di aggiungere, rimuovere e recuperare fermate e linee preferite
 * di ciascun utente. I dati sono salvati in file di testo per persistenza
 * tra esecuzioni.
 * </p>
 */
public class FavoritesService {

    // FILE DI MEMORIZZAZIONE ------------------------------------------------------------------------------------------
    private static final String STOPS_FILE = "data/favorites/favorites_stops.json";           // File dei preferiti per fermate.
    private static final String LINES_FILE = "data/favorites/favorites_lines.json";           // File dei preferiti per linee.

    // MAPPE DEI PREFERITI ---------------------------------------------------------------------------------------------
    private final Map<String, Set<String>> userStopFavorites = new HashMap<>();     // user → set di stop_id preferiti.
    private final Map<String, Set<String>> userLineFavorites = new HashMap<>();     // user → set di route_id preferite.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     */
    public FavoritesService() {
        loadFromFiles();                                // Carica i preferiti al momento della creazione del service.
    }

    // CARICA I PREFERITI DELL'UTENTE ----------------------------------------------------------------------------------
    /**
     * Inizializza le strutture dati dei preferiti per un utente se non presenti.
     *
     * @param username nome utente
     */
    public void loadFavorites(String username) {
        userStopFavorites.putIfAbsent(username, new HashSet<>());
        userLineFavorites.putIfAbsent(username, new HashSet<>());
    }

    // FERMATE ---------------------------------------------------------------------------------------------------------

    // AGGIUNGE O RIMUOVE UNA FERMATA DAI PREFERITI --------------------------------------------------------------------
    /**
     * Aggiunge o rimuove una fermata dai preferiti di un utente.
     *
     * @param username nome utente
     * @param stopId   ID della fermata
     */
    public void toggleFavoriteStop(String username, String stopId) {
        userStopFavorites.putIfAbsent(username, new HashSet<>());
        Set<String> favorites = userStopFavorites.get(username);

        if (favorites.contains(stopId)) {
            favorites.remove(stopId);
        } else {
            favorites.add(stopId);
        }
        saveToFiles();
    }

    // VERIFICA SE UNA FERMATA È NEI PREFERITI -------------------------------------------------------------------------
    /**
     * Verifica se una fermata è nei preferiti di un utente.
     *
     * @param username nome utente
     * @param stopId   ID della fermata
     * @return         true se è presente nei preferiti, false altrimenti
     */
    public boolean isStopFavorite(String username, String stopId) {
        return userStopFavorites.getOrDefault(username, Set.of()).contains(stopId);
    }

    // RESTITUISCE TUTTE LE FERMATE PREFERITE --------------------------------------------------------------------------
    /**
     * Restituisce tutte le fermate preferite di un utente.
     *
     * @param username nome utente
     * @return         insieme di ID delle fermate preferite
     */
    public Set<String> getFavoriteStops(String username) {
        return userStopFavorites.getOrDefault(username, Set.of());
    }

    // LINEE -----------------------------------------------------------------------------------------------------------

    // AGGIUNGE O RIMUOVE UNA LINEA DAI PREFERITI ----------------------------------------------------------------------
    /**
     * Aggiunge o rimuove una linea dai preferiti di un utente.
     *
     * @param username nome utente
     * @param trip     corsa attuale
     */
    public void toggleFavoriteLine(String username, Trip trip) {
        userLineFavorites.putIfAbsent(username, new HashSet<>());
        Set<String> favorites = userLineFavorites.get(username);

        String key = trip.getRouteId() + "_" + trip.getDirectionId();
        if (favorites.contains(key))
        {
            favorites.remove(key);
        } else {
            favorites.add(key);
        }
        saveToFiles();
    }

    // VERIFICA SE UNA LINEA È NEI PREFERITI ---------------------------------------------------------------------------
    /**
     * Verifica se una linea è nei preferiti di un utente.
     *
     * @param username nome utente
     * @param trip     corsa attuale
     * @return         true se è presente nei preferiti, false altrimenti
     */
    public boolean isLineFavorite(String username, Trip trip) {
        String key = trip.getRouteId() + "_" + trip.getDirectionId();
        return userLineFavorites.getOrDefault(username, Set.of()).contains(key);
    }

    // RESTITUISCE TUTTE LE LINEE PREFERITE ----------------------------------------------------------------------------
    /**
     * Restituisce tutte le linee preferite di un utente.
     *
     * @param username nome utente
     * @return         insieme di linee preferite
     */
    public Set<String> getFavoriteLines(String username) {
        return userLineFavorites.getOrDefault(username, Set.of());
    }

    // LETTURA/SCRITTURA DA FILE ---------------------------------------------------------------------------------------

    // CARICA TUTTI I PREFERITI DAI FILE (FERMATE E LINEE) -------------------------------------------------------------
    /**
     * Carica tutti i preferiti (fermate e linee) dai file.
     */
    private void loadFromFiles() {
        loadMapFromJson(STOPS_FILE, userStopFavorites);
        loadMapFromJson(LINES_FILE, userLineFavorites);
    }

    // SALVA TUTTI I PREFERITI SU FILE (FERMATE E LINEE) ---------------------------------------------------------------
    /**
     * Salva tutti i preferiti (fermate e linee) su file.
     */
    private void saveToFiles() {
        saveMapToJson(STOPS_FILE, userStopFavorites);
        saveMapToJson(LINES_FILE, userLineFavorites);
    }

    // METODO GENERICO PER CARICARE UNA MAPPA DA FILE ------------------------------------------------------------------
    /**
     * Carica una mappa di preferiti da file JSON.
     *
     * @param filePath percorso del file
     * @param map      mappa da popolare
     */
    private void loadMapFromJson(String filePath, Map<String, Set<String>> map) {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Map<String, Set<String>> loaded = gson.fromJson(reader,
                    new com.google.gson.reflect.TypeToken<Map<String, Set<String>>>() {}.getType());
            if (loaded != null) {
                map.clear();
                map.putAll(loaded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // METODO GENERICO PER SALVARE UNA MAPPA SU FILE -------------------------------------------------------------------
    /**
     * Salva una mappa di preferiti su file JSON.
     *
     * @param filePath percorso del file
     * @param map      mappa da salvare
     */
    private void saveMapToJson(String filePath, Map<String, Set<String>> map) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (Writer writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(map, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



