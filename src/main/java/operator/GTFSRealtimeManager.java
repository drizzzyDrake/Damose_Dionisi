package operator;

// Altre classi.
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

/**
 * Classe responsabile della gestione dei dati realtime.
 * <p>
 * Effettua il polling dei feed GTFS-realtime con retry/backoff semplice,
 * applicazione parziale dei feed quando possibile, e tracking della freschezza.
 * </p>
 */
public class GTFSRealtimeManager {

    // LOGGER ----------------------------------------------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(GTFSRealtimeManager.class.getName());    // Messaggi debug.

    // HTTP CLIENT -----------------------------------------------------------------------------------------------------
    private final HttpClient http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();                                                                           // Serve a fare richieste HTTP ai feed GTFS-realtime.

    // POLLER ----------------------------------------------------------------------------------------------------------
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "GTFSRealtimeManager-Poller");
        t.setDaemon(true);
        return t;                                                                               // Esegue il polling in background, senza bloccare il main thread.
    });

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private final GTFSDataIndexer indexer;

    // URL DEI FEED REALTIME -------------------------------------------------------------------------------------------
    private final URI tripUpdatesUri;
    private final URI vehiclePositionsUri;

    // PARAMETRI PER POLLING -------------------------------------------------------------------------------------------
    private final long baseIntervalSeconds = 30L;                                               // Intervallo normale: 30 secondi.
    private final long maxIntervalSeconds = 300L;                                               // Intervallo massimo (backoff): 5 minuti.
    private volatile long currentIntervalSeconds = baseIntervalSeconds;                         // Intervallo attuale, aggiornato in caso di failure.
    private ScheduledFuture<?> scheduledTask;                                                   // Future che rappresenta il task schedulato, utile per cancellarlo.

    // FAILURE TRACKING ------------------------------------------------------------------------------------------------
    private volatile int consecutiveFailures = 0;                                               // Tiene traccia di quante volte consecutive il polling non ha avuto successo.

    // FRESHNESS -------------------------------------------------------------------------------------------------------
    private volatile long lastSuccessfulEpochSec = 0L;                                          // Memorizza l’epoch-second dell’ultimo update riuscito (serve per sapere se i dati online sono freschi).

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param indexer             riferimento al GTFSDataIndexer
     * @param tripUpdatesUrl      URL feed GTFS TripUpdates
     * @param vehiclePositionsUrl URL feed GTFS VehiclePositions
     */
    public GTFSRealtimeManager(GTFSDataIndexer indexer, String tripUpdatesUrl, String vehiclePositionsUrl) {
        this.indexer = indexer;
        this.tripUpdatesUri = URI.create(tripUpdatesUrl);
        this.vehiclePositionsUri = URI.create(vehiclePositionsUrl);
    }

    // AVVIA IL POLLING ------------------------------------------------------------------------------------------------
    /**
     * Avvia il polling dei feed realtime. Se già in esecuzione non fa nulla.
     */
    public synchronized void start() {
        if (scheduledTask != null && !scheduledTask.isDone()) return;                                   // Sincronizzato: un task di polling alla volta.
        scheduleAtInterval(0, currentIntervalSeconds);                                  // Pianifica il prossimo task con intervallo corrente e 0 secondi di delay.
        LOG.info("GTFSRealtimeManager started (interval=" + currentIntervalSeconds + "s)");        // LOG: stampa l'intervallo attivo.
    }

    // ARRESTA IL POLLING ----------------------------------------------------------------------------------------------
    /**
     * Ferma il polling dei feed realtime.
     */
    public synchronized void stop() {
        if (scheduledTask != null) {                                        // Se il task è in corso...
            scheduledTask.cancel(false);                  // ...Cancella il task senza interrompere il thread in corso...
            scheduledTask = null;                                           // ...E elimina il riferimento del task.
        }
        LOG.info("GTFSRealtimeManager stopped");                       // LOG: stampa la conferma d'arresto.
    }

    // PIANIFICA UN NUOVO TASK DI POLLING ------------------------------------------------------------------------------
    /**
     * Pianifica un nuovo task di polling con intervallo specificato.
     *
     * @param initialDelaySeconds delay iniziale in secondi
     * @param intervalSeconds     intervallo tra poll in secondi
     */
    private synchronized void scheduleAtInterval(long initialDelaySeconds, long intervalSeconds) {
        if (scheduledTask != null) scheduledTask.cancel(false);                               // Cancella il task precedente (thread-safe)
        currentIntervalSeconds = intervalSeconds;                                                               // Aggiorna l'intervallo scelto.
        scheduledTask = scheduler.scheduleAtFixedRate(                                                          // Pianifica il prossimo task.
                this::pollOnce,                                                                                 // Inizializza un singolo ciclo di polling.
                initialDelaySeconds,                                                                            // Delay iniziale.
                intervalSeconds,                                                                                // Intervallo scelto.
                TimeUnit.SECONDS);                                                                              // Unità di misura per intervalli e delay (secondi).
    }

    // INIZIALIZZA UN SINGOLO CICLO DI POLLING -------------------------------------------------------------------------
    /**
     * Esegue un singolo ciclo di polling: scarica i feed, li applica all'indicizzatore e aggiorna lo stato di freschezza.
     */
    private void pollOnce() {
        try {
            FeedMessage tripFeed = null;                                                                                    // Accumulatore per tripUpdates.
            FeedMessage vehicleFeed = null;                                                                                 // Accumulatore per vehiclePosition.
            boolean anySuccess = false;                                                                                     // Flag di successo.

            // Fetch aggiornamenti sulle corse.
            byte[] tripBytes = fetchBytes(tripUpdatesUri);                                                                  // Tenta la richiesta HTTP sull’URI di tripUpdates.
            if (tripBytes != null) {                                                                                        // Se bytes non null (la richiesta ha avuto successo)...
                try {
                    tripFeed = FeedMessage.parseFrom(tripBytes);                                                            // ...Prova il parsing dei GTFS realtime scaricati.
                    anySuccess = true;                                                                                      // ...Marca flag di successo = true.
                } catch (Exception e) {
                    LOG.log(Level.FINE, "Parsing tripFeed fallito: " + e.getMessage(), e);                             // LOG: Parsing fallito.
                }
            }

            // Fetch posizione dei veicoli.
            byte[] vehicleBytes = fetchBytes(vehiclePositionsUri);                                                          // Tenta la richiesta HTTP sull’URI di vehiclePosition.
            if (vehicleBytes != null) {                                                                                     // Se bytes non null (la richiesta ha avuto successo)...
                try {
                    vehicleFeed = FeedMessage.parseFrom(vehicleBytes);                                                      // ...Prova il parsing dei GTFS realtime scaricati.
                    anySuccess = true;                                                                                      // ...Marca flag di successo = true.
                } catch (Exception e) {
                    LOG.log(Level.FINE, "Parsing vehicleFeed fallito: " + e.getMessage(), e);                          // LOG: Parsing fallito.
                }
            }

            // Se nessuno dei due fetch/parse ha avuto successo -> incrementa failure e applica backoff.
            if (!anySuccess) {                                                                                              // Se nessun fetch ha avuto successo...
                consecutiveFailures++;                                                                                      // ...Incrementa il contatore dei fallimenti consecutivi...
                long nextInterval = Math.min(                                                                               // ...Calcola un nuovo intervallo...
                        maxIntervalSeconds,                                                                                 // ...Intervallo massimo (5 minuti)...
                        baseIntervalSeconds * (1L << Math.min(consecutiveFailures, 6)));                                    // ...Nuovo intervallo: baseIntervalSeconds * 2^k con k = consecutiveFailures <= 6...
                if (nextInterval != currentIntervalSeconds) {                                                               // ...Se è stato cambiato intervallo (fetch fallito)...
                    LOG.info("Nessun feed valido, aumento interval da " +
                            currentIntervalSeconds + "s a " + nextInterval +
                            "s (failure count=" + consecutiveFailures + ")");                                               // ...LOG: nessun feed valido, aumento intervallo.
                    scheduleAtInterval(nextInterval, nextInterval);
                }
                LOG.fine("Polling realtime: nessun feed valido (consecutiveFailures=" + consecutiveFailures + ")");    // ...LOG: nessun feed valido, incremento fallimenti.
                return;
            }

            // Se almeno un feed è stato parsato con successo.
            consecutiveFailures = 0;                                                                                        // Azzera il numero di fallimenti consecutivi.
            if (currentIntervalSeconds != baseIntervalSeconds) {                                                            // Se l'intervallo non è più quello di base (fallimenti precedenti)...
                scheduleAtInterval(baseIntervalSeconds, baseIntervalSeconds);                                               // ...Ripristina l'intervallo di base...
                LOG.info("Feed tornati validi: ripristino interval base " + baseIntervalSeconds + "s");                // ...LOG: feed validi, ripristino intervallo.
            }
            indexer.applyRealtimeUpdates(tripFeed, vehicleFeed);                                                            // Applica i feed (anche se solo uno è disponibile, applyRealtimeUpdates gestisce null).
            lastSuccessfulEpochSec = Instant.now().getEpochSecond();                                                        // Aggiorna timestamp di successo (monitoraggio della freschezza).

        } catch (Exception e) {
            LOG.log(Level.WARNING, "Errore durante polling realtime (eccezione): " + e.getMessage(), e);               // LOG: errore generico nel polling dei dati GTFS realtime.
            consecutiveFailures++;                                                                                          // Incrementa i fallimenti consecutivi.
        }
    }

    // HELPER PER SCARICARE BYTE DA UN URI -----------------------------------------------------------------------------
    /**
     * Scarica i byte da un URI tramite HttpClient.
     *
     * @param uri URI da cui scaricare i dati
     * @return    array di byte scaricati o null in caso di errore
     */
    private byte[] fetchBytes(URI uri) {
        try {
            HttpRequest req = HttpRequest.newBuilder()                                                      // Costruzione della richiesta.
                    .uri(uri)                                                                               // Imposta l'URI.
                    .timeout(Duration.ofSeconds(10))                                                        // Timeout massimo di 10 secondi (altrimenti genera eccezione).
                    .GET()                                                                                  // GET: scaricare risorse.
                    .build();                                                                               // Costriusce l'oggetto HttpRequest finale.
            HttpResponse<byte[]> resp = http.send(req, HttpResponse.BodyHandlers.ofByteArray());            // Invia la richiesta e legge la risposta come array di bytes.
            if (resp.statusCode() == 200) return resp.body();                                               // Se status code = 200 = OK (es. 404 = not found) torna il contenut della risposta.
            LOG.fine("HTTP status non-ok per " + uri + ": " + resp.statusCode());                      // LOG: status code e corpo della risposta.
        } catch (Exception e) {
            LOG.log(Level.FINE, "fetchBytes fallita per " + uri + ": " + e.getMessage());              // LOG: richiesta fallita.
        }
        return null;                                                                                        // Backoff.
    }
}


