package service;

// Model.
import model.User;

// Classi utili.
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;

/**
 * Classe service per la gestione dell'autenticazione degli utenti.
 * <p>
 * Permette di registrare utenti, verificare se un utente è già registrato
 * e controllare le credenziali di login.
 * </p>
 */
public class AuthService {

    // File dove vengono salvati gli utenti registrati.
    private final File userFile = new File("data/auth/users.json");

    // File dove viene salvagta la sessione dell'utente corrente.
    private static final String SESSION_FILE = "data/auth/session.json";

    // CONTROLLA SE L'UTENTE E' GIA REGISTRATO -------------------------------------------------------------------------
    /**
     * Controlla se un determinato username è già registrato.
     *
     * @param username     username da registrare
     * @return             true se l'utente esiste, false altrimenti
     * @throws IOException se si verifica un errore di lettura del file
     */
    public boolean isRegistered(String username) throws IOException {
        return loadUsers().stream().anyMatch(u -> u.getUsername().equals(username));
    }

    // VERIFICA SE LE CREDENZIALI CORRISPONDONO ------------------------------------------------------------------------
    /**
     * Verifica che username e password corrispondano a un utente registrato.
     *
     * @param username     username dell'utente
     * @param password     password dell'utente
     * @return             true se le credenziali sono corrette, false altrimenti
     * @throws IOException se si verifica un errore di lettura del file
     */
    public boolean login(String username, String password) throws IOException {
        return loadUsers().stream().anyMatch(u -> u.getUsername().equals(username) && u.getPassword().equals(password));
    }

    // REGISTRA UN NUOVO UTENTE ----------------------------------------------------------------------------------------
    /**
     * Registra un nuovo utente scrivendo le credenziali nel file.
     *
     * @param username     username da registrare
     * @param password     password da registrare
     * @return             true se la registrazione ha successo, false se l'utente esiste già
     * @throws IOException se si verifica un errore di scrittura sul file
     */
    public boolean register(String username, String password) throws IOException {

        if (isRegistered(username)) return false;

        List<User> users = loadUsers();
        users.add(new User(username, password));

        saveUsers(users);
        return true;
    }

    // CARICA TUTTI GLI UTENTI REGISTRATI NEL FILE ---------------------------------------------------------------------
    /**
     * Carica tutti gli utenti registrati dal file users.txt.
     *
     * @return             lista di {@link User}
     * @throws IOException se si verifica un errore di lettura del file
     */
    private List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        if (!userFile.exists()) return users;

        try (Reader reader = new FileReader(userFile)) {
            Gson gson = new Gson();
            List<User> loaded = gson.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            if (loaded != null) users.addAll(loaded);
        }
        return users;
    }

    // SALVA TUTTI GLI UTENTI NEL FILE ---------------------------------------------------------------------------------
    /**
     * Salva tutti gli utenti registrati nel file users.json.
     *
     * @param users lista di utenti da salvare
     * @throws IOException se si verifica un errore di scrittura sul file
     */
    private void saveUsers(List<User> users) throws IOException {
        userFile.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(userFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(users, writer);
        }
    }

    // SALVA LA SESSIONE DELL'UTENTE -----------------------------------------------------------------------------------
    /**
     * Salva la sessione dell'utente corrente scrivendo l'username
     * nel file di sessione in formato JSON.
     *
     * @param username username dell'utente da salvare nella sessione
     */
    public void saveSession(String username) {
        try (Writer writer = new FileWriter(SESSION_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<String, String> session = new HashMap<>();
            session.put("username", username);
            gson.toJson(session, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CARICA LA SESSIONE DELL'UTENTE ----------------------------------------------------------------------------------
    /**
     * Carica la sessione dell'utente corrente leggendo l'username
     * dal file di sessione JSON.
     *
     * @return username salvato nella sessione, oppure null se il file non esiste
     */
    public String loadSession() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) return null;
        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Map<String, String> session = gson.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
            return session != null ? session.get("username") : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // CANCELLA LA SESSIONE DELL'UTENTE --------------------------------------------------------------------------------
    /**
     * Cancella la sessione dell'utente corrente eliminando
     * il file di sessione.
     */
    public void clearSession() {
        new File(SESSION_FILE).delete();
    }
}




