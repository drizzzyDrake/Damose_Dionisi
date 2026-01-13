package controller;

// Service.
import service.AuthService;

// Altre classi.
import java.io.IOException;

/**
 * Controller per la gestione del login e della registrazione utenti.
 */
public class LoginController {

    // SERVICE ---------------------------------------------------------------------------------------------------------
    private final AuthService authService;              // Servizio di gestione degli utenti.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private String currentUser;
    private String currentPassword;

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param authService service per l'autenticazione
     */
    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    // CONTROLLA CHE LE CREDENZIALI IN INPUT SIANO VALIDE --------------------------------------------------------------
    /**
     * Effettua il login dell'utente.
     *
     * @param username     username dell'utente
     * @param password     password dell'utente
     * @return             true se il login ha successo, false altrimenti
     * @throws IOException in caso di problemi con il servizio di autenticazione
     */
    public boolean login(String username, String password) throws IOException {
        currentUser = username;                         // Assegno l'username a l'utente corrente.
        currentPassword = password;                     // Assegno la password all'utente corrente
        return authService.login(username, password);   // Ritorna true se il login ha successo.
    }

    // CONTROLLA SE L'UTENTE E' GIA REGISTRATO -------------------------------------------------------------------------
    /**
     * Verifica se un utente è già registrato.
     *
     * @param username     username dell'utente
     * @return             true se l'utente è già registrato, false altrimenti
     * @throws IOException in caso di problemi con il servizio di autenticazione
     */
    public boolean isRegistered(String username) throws IOException {
        return authService.isRegistered(username);  // Ritorna true se l'utente è gia registrato.
    }

    // PROVA A REGISTRARE UN NUOVO UTENTE ------------------------------------------------------------------------------
    /**
     * Registra un nuovo utente.
     *
     * @param username     username dell'utente
     * @param password     password dell'utente
     * @return             true se la registrazione è andata a buon fine, false altrimenti
     * @throws IOException in caso di problemi con il servizio di autenticazione
     */
    public boolean register(String username, String password) throws IOException {
        return authService.register(username, password);    // Ritorna true se la registrazione è andata a buon fine.
    }

    // RESTITUISCE L'UTENTE ATTUALMENTE LOGGATO ------------------------------------------------------------------------
    /**
     * Restituisce l'username dell'utente attualmente loggato.
     *
     * @return username corrente
     */
    public String getCurrentUser() {
        return currentUser;
    }

    // RESTITUISCE LA PASSWORD DELL'UTENTE ATTUALMENTE LOGGATO ---------------------------------------------------------
    /**
     * Restituisce la password dell'utente attualmente loggato.
     *
     * @return password corrente
     */
    public String getCurrentPassword() {
        return currentPassword;
    }
}

