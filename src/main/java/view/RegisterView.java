package view;

// Controller.
import controller.LoginController;

// Layout e Nodi.
import javafx.scene.image.Image;
import view.layouts.RegisterLayout;
import view.nodes.RegisterNode;

// Classi JavaFX necessarie per mostrare la GUI.
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Classe view per la zona di registrazione.
 * <p>
 * Compone la GUI della zona di registrazione.
 */
public class RegisterView {

    // STAGE -----------------------------------------------------------------------------------------------------------
    private final Stage registerStage;                  // Finestra principale (passata da LoginView).

    // SCENA -----------------------------------------------------------------------------------------------------------
    private final Scene registerScene;                  // Scena della finestra di registrazione.

    // ROOT ------------------------------------------------------------------------------------------------------------
    private final VBox registerRoot;                    // Layout della registrazione.

    // NODI E LAYOUT ---------------------------------------------------------------------------------------------------
    private final RegisterNode registerNode;            // Nodi della registrazione.
    private final RegisterLayout registerLayout;        // Layout della registrazione.

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore della view di registrazione.
     *
     * @param ownerStage stage principale (da LoginView).
     * @param controller controller del login.
     */
    public RegisterView(Stage ownerStage,
                        LoginController controller) {
        this.registerNode = new RegisterNode();
        this.registerLayout = new RegisterLayout();

        // CREAZIONE DELLA FINESTRA ------------------------------------------------------------------------------------

        registerStage = new Stage(StageStyle.TRANSPARENT);
        registerStage.initModality(Modality.APPLICATION_MODAL);
        registerStage.initOwner(ownerStage);

        // CREAZIONE COMPONENTI PER LA FINESTRA DI REGISTRAZIONE -------------------------------------------------------

        // Campi username, password e conferma password.
        TextField usernameField = registerNode.createUsernameField();
        PasswordField passwordField = registerNode.createPasswordField();
        TextField visiblePasswordField = registerNode.createVisiblePasswordField();
        PasswordField confirmPasswordField = registerNode.createPasswordField();
        confirmPasswordField.setPromptText("Conferma Password");
        TextField confirmVisiblePasswordField = registerNode.createVisiblePasswordField();

        // Sincronizzo i contenuti tra passwordField e visiblePasswordField.
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());

        // Sincronizzo i contenuti tra confirmPasswordField e confirmVisiblePasswordField.
        confirmPasswordField.textProperty().bindBidirectional(confirmVisiblePasswordField.textProperty());

        // Bottone per rendere la password visibile/nascosta.
        Button visiblePasswordButton = registerNode.createVisiblePasswordButton();
        visiblePasswordButton.setOnAction(_ -> {
            boolean showing = visiblePasswordField.isVisible();
            visiblePasswordField.setVisible(!showing);
            visiblePasswordField.setManaged(!showing);
            passwordField.setVisible(showing);
            passwordField.setManaged(showing);
        });

        // Bottone per rendere la conferma della password visibile/nascosta.
        Button confirmVisiblePasswordButton = registerNode.createVisiblePasswordButton();
        confirmVisiblePasswordButton.setOnAction(_ -> {
            boolean showing = confirmVisiblePasswordField.isVisible();
            confirmVisiblePasswordField.setVisible(!showing);
            confirmPasswordField.setManaged(!showing);
            confirmPasswordField.setVisible(showing);
            confirmPasswordField.setManaged(showing);
        });

        // Messaggio di stato.
        Label message = registerNode.createMessageLabel();

        // Bottone per registrarsi.
        Button registerButton = registerNode.createRegisterButton();
        registerButton.setOnAction(_ -> {

            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();
            String confirm = confirmPasswordField.getText().trim();

            if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                message.setText("✕ Tutti i campi sono obbligatori!");
                return;
            }

            if (!pass.equals(confirm)) {
                message.setText("✕ Le password non coincidono!");
                return;
            }

            try {
                if (controller.isRegistered(user)) {
                    message.setText("✕ Utente già registrato!");
                } else if (controller.register(user, pass)) {
                    message.setText("✓ Registrazione completata!");
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                        javafx.application.Platform.runLater(registerStage::close);
                    }).start();
                } else {
                    message.setText("✕ Errore durante la registrazione.");
                }
            } catch (Exception e) {
                message.setText("✕ Errore interno.");
            }
        });

        // Bottone per tornare al login.
        Button cancelButton = registerNode.createCancelButton();
        cancelButton.setOnAction(_ -> registerStage.close());

        // ASSEMBLAGGIO DEL LAYOUT -------------------------------------------------------------------------------------

        StackPane passwordPane = registerLayout.createPasswordPane(passwordField, visiblePasswordField, visiblePasswordButton);
        StackPane confirmPasswordPane = registerLayout.createPasswordPane(confirmPasswordField, confirmVisiblePasswordField, confirmVisiblePasswordButton);
        HBox registerBox = registerLayout.createRegisterBox(registerButton, cancelButton);

        registerRoot = registerLayout.createRegisterRoot(usernameField, passwordPane, confirmPasswordPane, message, registerBox);

        // SETTAGGIO DELLA SCENA ---------------------------------------------------------------------------------------

        registerScene = new Scene(registerRoot, 350, 250);
        registerScene.setFill(null);

        // SETTAGGIO DELLA FINESTRA ------------------------------------------------------------------------------------

        registerStage.setScene(registerScene);
        registerStage.setTitle("Registration");
        registerStage.setResizable(false);

    }

    // SHOW DELLA FINESTRA ---------------------------------------------------------------------------------------------
    /**
     * Mostra la finestra di registrazione in modalità thread-safe.
     */
    public void show() { Platform.runLater(registerStage::show); }
}


