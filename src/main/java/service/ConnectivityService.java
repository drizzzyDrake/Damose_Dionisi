package service;

// Operator.
import operator.GTFSRealtimeManager;

// Altre classi.
import javafx.application.Platform;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Classe service per il monitoraggio periodico dello stato della connessione Internet.
 * <p>
 * Verifica lo stato della connessione ogni pochi secondi e
 * gestisce l'avvio/arresto del servizio realtime in base alla disponibilità di rete.
 * Permette inoltre di notificare le classi interessate al cambio di stato tramite listener.
 * </p>
 */
public class ConnectivityService {

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private final GTFSRealtimeManager realtimeManager;      // Gestore dei dati realtime.

    // THREAD ----------------------------------------------------------------------------------------------------------
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();    // Thread che esegue controlli ogni pochi secondi.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private volatile boolean wasOnline;                     // Memorizza l'ultimo stato di connessione rilevato.
    private Consumer<Boolean> onConnectionChange;           // Funzione listener di notifica al cambio di connessione.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param realtimeManager gestore dei dati realtime
     */
    public ConnectivityService(GTFSRealtimeManager realtimeManager) {
        this.realtimeManager = realtimeManager;
    }

    // AVVIA IL MONITORAGGIO PERIODICO DELLA CONNESSIONE ---------------------------------------------------------------
    /**
     * Avvia il monitoraggio periodico dello stato della connessione.
     * <p>
     * Se la connessione è disponibile all'avvio, avvia immediatamente
     * il servizio realtime e notifica lo stato al listener registrato.
     * </p>
     */
    public void start() {

        wasOnline = NetworkService.isOnline();      // Ottiene lo stato della connessione all'avvio.

        if (wasOnline) {                            // Se online...
            realtimeManager.start();                // ...Avvia il servizio realtime...
            notifyChange(true);                     // ...E notifica lo stato al listener.
        }

        // Controllo periodico della connessione ogni 5 secondi con checkConnection().
        scheduler.scheduleAtFixedRate(this::checkConnection, 5, 5, TimeUnit.SECONDS);
    }

    // ARRESTA IL MONITORAGGIO PERIODICO DELLA CONNESSIONE -------------------------------------------------------------
    /**
     * Ferma il monitoraggio periodico dello stato della connessione.
     */
    public void stop() {
        scheduler.shutdownNow();                            // Arresta il controllo periodico dello stato.
    }

    // CONTROLLA LO STATO DELLA CONNESSIONE A OGNI CICLO ---------------------------------------------------------------
    /**
     * Controlla lo stato attuale della connessione.
     * <p>
     * Se lo stato della connessione è cambiato rispetto all'ultimo controllo,
     * avvia o arresta il servizio realtime e notifica il listener registrato.
     * </p>
     */
    private void checkConnection() {

        boolean isOnline = NetworkService.isOnline();       // Ottiene lo stato attuale della connessione.

        if (isOnline != wasOnline) {                        // Se lo stato è cambiato...
            wasOnline = isOnline;                           // ...Salva il nuovo stato.

            if (isOnline) realtimeManager.start();          // Se online avvia il servizio realtime.
            else realtimeManager.stop();                    // Altrimenti lo arresta.

            notifyChange(isOnline);                         // Notifica il cambiamento di stato al listener.
        }
    }

    // RITORNA SUBITO L'ULTIMO STATO NOTO SENZA ASPETTARE IL PROSSIMO CONTROLLO ----------------------------------------
    /**
     * Restituisce l'ultimo stato noto della connessione.
     *
     * @return true se la connessione è attiva, false altrimenti
     */
    public boolean isCurrentlyOnline() {
        return wasOnline;
    }

    // REGISTRA LE CLASSI LISTENER AL SERVIZIO -------------------------------------------------------------------------
    /**
     * Registra un listener che verrà notificato ad ogni cambio di stato della connessione.
     *
     * @param listener funzione Consumer&lt;Boolean&lt; che riceve true se la connessione è attiva, false altrimenti
     */
    public void setOnConnectionChange(Consumer<Boolean> listener) {
        this.onConnectionChange = listener;
    }

    // NOTIFICA LE CLASSI LISTENER DEL CAMBIO DI CONNESSIONE -----------------------------------------------------------
    /**
     * Notifica il listener registrato del cambio di stato della connessione.
     *
     * @param isOnline stato attuale della connessione
     */
    private void notifyChange(boolean isOnline) {
        if (onConnectionChange != null) {
            Platform.runLater(() -> onConnectionChange.accept(isOnline));
        }
    }
}


