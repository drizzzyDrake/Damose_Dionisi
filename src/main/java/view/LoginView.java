package view;

// Controller.
import controller.LoginController;

// Layout e Nodi.
import javafx.scene.image.Image;
import view.layouts.LoginLayout;
import view.layouts.TitleBarLayout;
import view.nodes.LoginNode;
import view.nodes.TitleBarNode;

// Classi JavaFX necessarie per mostrare la GUI.
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

// Altre classi
import java.io.IOException;

/**
 * Classe view per la finestra di login.
 * <p>
 * Compone la GUI della finestra di login.
 */
public class LoginView {

    // STAGE -----------------------------------------------------------------------------------------------------------
    private final Stage loginStage;                 // Finestra principale (passata da app.Main).

    // SCENA -----------------------------------------------------------------------------------------------------------
    private Scene loginScene;                       // Scena della finestra di login.

    // ROOT ------------------------------------------------------------------------------------------------------------
    private BorderPane loginRoot;                   // Layout del login.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final TitleBarNode titleBarNode;        // Nodi della titlebar.
    private final LoginNode loginNode;              // Nodi del login.
    private final TitleBarLayout titleBarLayout;    // Layout della titlebar.
    private final LoginLayout loginLayout;          // Layout della zona di login.

    // CONTROLLER ------------------------------------------------------------------------------------------------------
    private final LoginController controller;       // Controller del login.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private final Runnable onLoginSuccess;          // Callback da eseguire in caso di riuscita del login (passata da app.Main).

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param loginStage     finestra principale.
     * @param onLoginSuccess callback da eseguire al login riuscito.
     * @param controller     controller per gestire autenticazione e registrazione.
     */
    public LoginView(Stage loginStage,
                     Runnable onLoginSuccess,
                     LoginController controller) {
        this.loginStage = loginStage;
        this.onLoginSuccess = onLoginSuccess;
        this.controller = controller;
        this.titleBarNode = new TitleBarNode();
        this.loginNode = new LoginNode();
        this.titleBarLayout = new TitleBarLayout();
        this.loginLayout = new LoginLayout();
    }

    // CREAZIONE DELLA UI ----------------------------------------------------------------------------------------------
    /**
     * Crea l'interfaccia della finestra di login e assembla tutti i layout e le view.
     */
    public void createView(){

        // CREAZIONE COMPONENTI PER LA TITLEBAR ------------------------------------------------------------------------

        // Bottone per la riduzione a icona.
        Button minimizeButton = titleBarNode.createMinimizeButton();
        minimizeButton.setOnAction(_ -> loginStage.setIconified(true));

        // Bottone per il full screen.
        Button maximizeButton = titleBarNode.createMaximizeButton();
        maximizeButton.setOnAction(_ -> {
            if (loginStage.isFullScreen()) {                // Se la finestra è in full screen...
                loginStage.setFullScreen(false);            // ...Esce dal full screen...
                loginStage.setResizable(true);              // ...Ora può essere ridimensionata.
            } else {                                        // Se la finestra non è in full screen...
                loginStage.setFullScreen(true);             // ...Entra in full screen...
                loginStage.setResizable(false);             // ...Disattiva ridimensionamento.
            }
        });
        maximizeButton.setDisable(true);                    // Disattivo la funzionalità per questa finestra.
        maximizeButton.setOpacity(0.5);

        // Bottone per la chiusura della finestra.
        Button closeButton = titleBarNode.createCloseButton();
        closeButton.setOnAction(_ -> loginStage.close());

        // CREAZIONE COMPONENTI PER IL LOGIN E LA REGISTRAZIONE -------------------------------------------------------

        // Campi per username e password.
        TextField usernameField = loginNode.createUsernameField();
        PasswordField passwordField = loginNode.createPasswordField();
        TextField visiblePasswordField = loginNode.createVisiblePasswordField();

        // Sincronizzo i contenuti tra passwordField e visiblePasswordField.
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());

        // Bottone per rendere visibile la password.
        Button visiblePasswordButton = loginNode.createVisiblePasswordButton();
        visiblePasswordButton.setOnAction(_ -> {
            boolean showing = visiblePasswordField.isVisible();
            visiblePasswordField.setVisible(!showing);
            visiblePasswordField.setManaged(!showing);
            passwordField.setVisible(showing);
            passwordField.setManaged(showing);
        });

        // Etichetta con messaggio d'errore.
        Label message = loginNode.createMessageLabel();

        // Bottone per effettuare il login.
        Button loginButton = loginNode.createLoginButton(usernameField, passwordField, message);
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(_ -> {

            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                message.setText("✕ Inserisci username e password!");
                return;
            }

            try {
                if (!controller.isRegistered(user)) {
                    message.setText("✕ Utente non registrato. Registrati prima!");
                } else if (controller.login(user, pass)) {
                    message.setText("✓ Login riuscito!");
                    onLoginSuccess.run();
                } else {
                    message.setText("✕ Password errata.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Bottone per aprire la finestra di registrazione.
        Button registerButton = loginNode.createRegisterButton(usernameField, passwordField, message);
        registerButton.setOnAction(_ -> {
            RegisterView registerView = new RegisterView(loginStage, controller);
            registerView.show();
        });

        // ASSEMBLAGGIO DEL LAYOUT -------------------------------------------------------------------------------------

        HBox titleBar = titleBarLayout.createTitleBarBox(minimizeButton, maximizeButton, closeButton);
        StackPane passwordPane = loginLayout.createPasswordPane(passwordField, visiblePasswordField, visiblePasswordButton);
        VBox loginBox = loginLayout.createLoginBox(usernameField, passwordPane, loginButton, message);
        HBox registerBox = loginLayout.createRegisterBox(registerButton);

        loginRoot = loginLayout.createLoginRoot(titleBar, loginBox, registerBox);

        // SETTAGGIO DELLA SCENA ---------------------------------------------------------------------------------------

        loginScene = new Scene(loginRoot, 400, 300);
        loginScene.setFill(null);

        // SETTAGGIO DELLA FINESTRA ------------------------------------------------------------------------------------

        loginStage.setScene(loginScene);
        loginStage.setTitle("Login");

    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Mostra la finestra di login.
     * Se la scena non è ancora stata creata, la crea prima.
     */
    public void show() {
        if (loginScene == null) {
            createView();
        }
        // Forza le dimensioni e la posizone per callback di logout.
        loginStage.setMinWidth(0);
        loginStage.setMinHeight(0);
        loginStage.sizeToScene();
        loginStage.centerOnScreen();
        loginStage.show();
    }

}
