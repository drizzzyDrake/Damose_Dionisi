package app;

// Controller.
import controller.*;

// Service.
import service.*;
import static service.NetworkService.isOnline;

// operator.
import operator.*;

// Componenti della GUI.
import view.ErrorView;
import view.HomeView;
import view.LoadingView;
import view.LoginView;

// Classi javaFx.
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;

import javax.swing.*;

/**
 * Classe main dell'applicazione.
 * <p>
 * Avvia l'applicazione
 * e inizializza operatori, servizi, view e controller.
 * </p>
 */
public class Main extends Application {

    private String currentUser;                 // Utente corrente.
    private String lastUser;                    // Utente salvato da sessione precedente.

    private Runnable onLoginSuccess;            // Runnable di verifica del successo del login.

    // Collegamenti per i file dei dati realtime.
    private final String TRIPUPDATE_URL = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    private final String VEHICLEPOS_URL = "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";

    // Campi operator.
    private GTFSDataIndexer dataIndexer;
    private GTFSRealtimeManager realtimeManager;
    private TilesManager tilesManager;

    // Campi service.
    private ConnectivityService connectivityService;
    private FavoritesService favoritesService;
    private AuthService authService;

    // START -----------------------------------------------------------------------------------------------------------
    /**
     * Metodo principale chiamato da JavaFX per avviare l'applicazione.
     * <p>
     * Configura lo stage principale, carica dati GTFS,
     * gestisce la connessione e mostra le view di login/home.
     * </p>
     *
     * @author             Giulio Dionisi
     * @param primaryStage Stage principale dell'applicazione.
     */
    public void start(Stage primaryStage) {

        // STAGE PRINCIPALE --------------------------------------------------------------------------------------------

        // Imposto lo stage che passerò a loadingView, errorView, loginView e mainView.
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Damose Dionisi");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/damose-main.png")));

        Platform.setImplicitExit(true);                                     // Se si chiude l’ultimo stage termina l’app e invoca stop().

        // OPERATOR LOADER ---------------------------------------------------------------------------------------------

        GTFSStaticLoader dataLoader = new GTFSStaticLoader();               // Carica i dati GTFS da cache o resources.

        // CONTROLLO DELLA CONNESSIONE E AGGIORNAMENTO DELLA CACHE -----------------------------------------------------

        // Mostra la finestra di caricamento.
        LoadingView loadingView = new LoadingView(primaryStage);
        loadingView.show();
        loadingView.updateMessage("Verica connessione...");

        new Thread(() -> {              // Nuovo thread per eseguire operazioni lente (rete, I/O) senza bloccare l’interfaccia grafica.
            try {
                // 1. Controllo connessione e aggiornamento cache.
                if (isOnline()) {                                                                           // Se il sistema è online...
                    loadingView.updateMessage("ONLINE\n");                                                  // ...La finestra di caricamento mostra il messaggio...
                    if (!GTFSCacheManager.isCacheAvailable()) {                                             // Se non è stata ancora creata una cache con i dati GTFS statici...
                        GTFSCacheManager.updateCacheIfOnline();                                             // ...La cache con i file GTFS viene aggiornata.
                    }
                } else {                                                                                    // Se il sistema è offline...
                    loadingView.updateMessage("OFFLINE\n");                                                 // ...La finestra di caricamento mostra il messaggio.
                }

                // CARICAMENTO DATI GTFS -------------------------------------------------------------------------------

                // 2. Caricamento effettivo dei dati.
                if (GTFSCacheManager.isCacheAvailable()) {                                                   // Se la cache è disponibile...
                    loadingView.updateMessage("caricamento GTFS dalla cache locale...");                     // ...La finestra di caricamento mostra il messaggio...
                    dataLoader.loadAllFromDirectory(GTFSCacheManager.getCacheDirectory());                   // ...il GTFSStaticLoader carica i dati dalla cache, situata nella home utente.
                } else {                                                                                     // Se la cache non è disponibile (primo avvio)...
                    loadingView.updateMessage("CACHE NON TROVATA\naggiornamento della cache...");            // ...La finestra di caricamento mostra il messaggio...
                    throw new RuntimeException("Cache GTFS non disponibile e nessuna connessione");          // ...Genera l'eccezione.
                }

                // OPERATOR INDEXER ------------------------------------------------------------------------------------

                dataIndexer = new GTFSDataIndexer(dataLoader);                                              // Crea le relazioni tra le varie entità GTFS.
                realtimeManager = new GTFSRealtimeManager(dataIndexer, TRIPUPDATE_URL, VEHICLEPOS_URL);     // Si occupa della gestione dei dati realtime.

                // SERVICE ---------------------------------------------------------------------------------------------

                favoritesService = new FavoritesService();                                                  // Gestisce i preferiti.
                authService = new AuthService();                                                            // Gestisce l'autenticazione.
                lastUser = authService.loadSession();                                                       // Carica eventuale utente salvato.
                connectivityService = new ConnectivityService(realtimeManager);                             // Gestisce la connessione.
                connectivityService.start();                                                                // Avvia il servizio realtime.

                // CONTROLLER ------------------------------------------------------------------------------------------

                LoginController loginController = new LoginController(authService);
                MapsController mapsController = new MapsController(dataIndexer);
                FindStopController findStopController = new FindStopController(dataIndexer);
                StopInfoController stopInfoController = new StopInfoController(dataIndexer, favoritesService);
                FindLineController findLineController = new FindLineController(dataIndexer);
                LineInfoController lineInfoController = new LineInfoController(dataIndexer, favoritesService);
                ToolBarController toolBarController = new ToolBarController(dataIndexer, favoritesService, connectivityService);

                // FINE DEL CARICAMENTO --------------------------------------------------------------------------------

                // 3. Dopo il caricamento chiudo la finestra e passo al login (se non esiste una sessione salvata).
                Platform.runLater(() -> {           // Indica che il codice seguente viene eseguito nel JavaFX Application Thread, cioè il thread della GUI (principale).
                    loadingView.close();            // Chiusura della finestra di caricamento.

                    // FINESTRA HOME -----------------------------------------------------------------------------------

                    // 4. Se il login ha avuto successo passo la finestra alla home dell'app.
                    onLoginSuccess = () -> {

                        currentUser = loginController.getCurrentUser();         // Salvo l'username dell'utente corrente.
                        authService.saveSession(currentUser);                   // Salvo la sessione utente.
                        stopInfoController.setCurrentUser(currentUser);         // Passo l'utente attuale a stopInfoController.
                        lineInfoController.setCurrentUser(currentUser);         // Passo l'utente attuale a lineInfoController.

                        toolBarController.setCurrentUser(currentUser);          // Passo l'utente attuale a toolBarController.
                        HomeView homeView = new HomeView(
                                primaryStage,
                                currentUser,
                                mapsController,
                                findStopController,
                                stopInfoController,
                                findLineController,
                                lineInfoController,
                                toolBarController);

                        dataIndexer.addRealtimeListener(() ->
                                SwingUtilities.invokeLater(() ->
                                        homeView.getMapsView().refreshVehiclesLayer()
                                )
                        );

                        Runnable onLogout = () -> {
                            currentUser = null;                                     // Cancella l'utente corrente.
                            authService.clearSession();                             // Cancella la sessione utente.

                            LoginView loginView = new LoginView(primaryStage, onLoginSuccess, loginController);
                            loginView.show();
                        };
                        homeView.getToolBarView().setOnLogout(onLogout);
                        homeView.show();
                    };

                    // FINESTRA LOGIN / HOME ----------------------------------------------------------------------------------

                    if (lastUser != null) {
                        // 3. Se esiste una sessione salvata passo subito alla home
                        currentUser = lastUser;
                        stopInfoController.setCurrentUser(currentUser);
                        lineInfoController.setCurrentUser(currentUser);
                        toolBarController.setCurrentUser(currentUser);

                        HomeView homeView = new HomeView(
                                primaryStage,
                                currentUser,
                                mapsController,
                                findStopController,
                                stopInfoController,
                                findLineController,
                                lineInfoController,
                                toolBarController);

                        dataIndexer.addRealtimeListener(() ->
                                SwingUtilities.invokeLater(() ->
                                        homeView.getMapsView().refreshVehiclesLayer()
                                )
                        );

                        Runnable onLogout = () -> {
                            currentUser = null;                                     // Cancella l'utente corrente.
                            authService.clearSession();                             // Cancella la sessione utente.

                            LoginView loginView = new LoginView(primaryStage, onLoginSuccess, loginController);
                            loginView.show();
                        };
                        homeView.getToolBarView().setOnLogout(onLogout);
                        homeView.show();

                    } else {
                        // 3. Altrimenti mostra la finestra di login.
                        LoginView loginView = new LoginView(primaryStage, onLoginSuccess, loginController);
                        loginView.show();
                    }
                });

            } catch (
                    Exception e) {                                          // In caso di errore nel caricamento dei dati mostra la finestra d'errore e blocca il programma.
                Platform.runLater(() -> {                                   // Indica che il codice seguente viene eseguito nel JavaFX Application Thread, cioè il thread della GUI (principale).
                    loadingView.close();                                    // Chiusura della finestra di caricamento.
                    ErrorView errorView = new ErrorView(primaryStage);
                    errorView.updateMessage("ERRORE NEL CARICAMENTO DEI DATI:\n" + e.getMessage());
                    e.printStackTrace();
                    errorView.show();
                });
            }
        }).start();
    }

    // STOP ------------------------------------------------------------------------------------------------------------
    /**
     * Arresta e rilascia tutte le risorse utilizzate dall'applicazione.
     * <p>
     * Questo metodo viene invocato automaticamente dal ciclo di vita JavaFX e Swing
     * quando l'applicazione viene chiusa.
     * </p>
     */
    public void stop() {
        // Thread di JavaFx
        try {
            if (connectivityService != null) {
                connectivityService.stop();
            }
            if (dataIndexer != null) {
                dataIndexer.removeRealtimeListeners();
                dataIndexer.clear();
            }
            if (realtimeManager != null) {
                realtimeManager.stop();
            }
            if (tilesManager != null) {
                tilesManager.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Thread di Swing.
        javax.swing.SwingUtilities.invokeLater(() -> System.exit(0));
    }

    public static void main(String[] args) {
        launch(args);   // Avvia il ciclo di vita JavaFX e chiama start(Stage)
    }
}














