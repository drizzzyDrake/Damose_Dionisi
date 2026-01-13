package model;

/**
 * JavaBean che rappresenta un utente del sistema.
 * <p>
 * Contiene tutte le informazioni principali sull'utente.
 * </p>
 * <ul>
 *     <li>{@code username}: nome univoco scelto dall’utente</li>
 *     <li>{@code password}: password associata all’utente</li>
 * </ul>
 */
public class User {
    private String username;
    private String password;

    /**
     * Costruttore che inizializza un nuovo oggetto {@code User}.
     *
     * @param username il nome utente dell’utente
     * @param password la password associata all’utente
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters

    /**
     * Restituisce il nome utente.
     *
     * @return nome utente
     */
    public String getUsername() {
        return username;
    }

    /**
     * Restituisce la password dell’utente.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
}


