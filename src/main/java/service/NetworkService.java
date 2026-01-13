package service;

// Altre classi.
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

/**
 * Classe service per il controllo della connessione.
 * <p>
 * Tramite un test stabilisce se il dispositivo è online o offline.
 * </p>
 */
public class NetworkService {

    private static final String TEST_URL = "https://www.google.com";    // URL utilizzato per testare la connessione.
    private static final int TIMEOUT_MS = 2000;                         // Timeout di 2000 millisecondi (2 secondi) per la connessione.

    // COSTRUTTORE PRIVATO ---------------------------------------------------------------------------------------------
    /**
     * Costruttore privato per evitare istanziazioni della classe.
     */
    private NetworkService() {} // Evita istanziazione.

    // CONTROLLA LA CONNESSIONE ----------------------------------------------------------------------------------------
    /**
     * Verifica se il dispositivo è online provando a connettersi a un URL di test.
     *
     * @return true se la connessione ha successo (online), false altrimenti (offline)
     */
    public static boolean isOnline() {
        try {
            // Crea una connessione HTTP all’URL per il test.
            HttpURLConnection connection = (HttpURLConnection) new URL(TEST_URL).openConnection();
            connection.setConnectTimeout(TIMEOUT_MS);           // Imposta il timeout: se non si connette entro 2 secondi, fallisce.
            connection.connect();                               // Prova a connettersi.
            connection.disconnect();                            // Chiude la connessione.
            return true;                                        // Se tutto va bene il sistema è online.
        } catch (IOException e) {                               // Se durante la connessione si genera un'eccezione...
            return false;                                       // ...Il sistema è offline.
        }
    }
}
