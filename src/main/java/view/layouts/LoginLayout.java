package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Classe layout per il login.
 * <p>
 * Gestisce la disposizione dei nodi della zona login,
 * con campi per username e password, pulsanti di accesso e registrazione,
 * e messaggi di feedback.
 */
public class LoginLayout {

    /**
     * Crea la root del layout login.
     *
     * @param titleBar    Box contenente la barra del titolo
     * @param loginBox    Box contenente i campi e pulsanti del login
     * @param registerBox Box contenente il pulsante di registrazione
     * @return            BorderPane contenente tutte le sezioni del login
     */
    public BorderPane createLoginRoot(HBox titleBar, VBox loginBox, HBox registerBox){
        BorderPane loginRoot = new BorderPane();                 // Creo una borderpane per posizionare le tre box.
        loginRoot.setTop(titleBar);                              // Zona della titlebar in alto.
        loginRoot.setCenter(loginBox);                           // Zona del login al centro.
        loginRoot.setBottom(registerBox);                        // Zona della registrazione in basso.
        loginRoot.setStyle(
                "-fx-border-radius: 20;"+
                "-fx-background-radius: 20;"+
                "-fx-background-color: rgba(0,0,0,0.8);"
        );
        return loginRoot;
    }

    /**
     * Crea il layout per la barra della password visibile/nascosta.
     *
     * @param passwordField         Campo password nascosto
     * @param visiblePasswordField  Campo password visibile
     * @param visiblePasswordButton Pulsante per mostrare/nascondere la password
     * @return                      StackPane contenente i componenti della password
     */
    public StackPane createPasswordPane(PasswordField passwordField,
                                        TextField visiblePasswordField,
                                        Button visiblePasswordButton) {
        StackPane passwordPane = new StackPane();
        passwordPane.setStyle(
                "-fx-alignment: CENTER_RIGHT;"
        );
        // Compone il layout con i rispettivi bottoni.
        passwordPane.getChildren().addAll(passwordField, visiblePasswordField, visiblePasswordButton);
        return passwordPane;
    }

    /**
     * Crea il layout della zona login centrale.
     *
     * @param usernameField Campo per inserimento username
     * @param passwordPane  Pane della password (visibile/nascosta)
     * @param loginButton   Pulsante di login
     * @param message       Label per eventuali messaggi di errore
     * @return              VBox contenente i componenti della zona login
     */
    public VBox createLoginBox(TextField usernameField,
                               StackPane passwordPane,
                               Button loginButton,
                               Label message){
        VBox loginBox = new VBox(10);
        loginBox.setStyle(
                "-fx-padding: 10;"+
                "-fx-alignment: CENTER_LEFT;"
        );
        // Compone il layout con i rispettivi bottoni.
        loginBox.getChildren().addAll(usernameField, passwordPane, loginButton, message);
        return loginBox;
    }

    /**
     * Crea il layout della zona di registrazione.
     *
     * @param registerButton Pulsante per registrazione
     * @return               HBox contenente il pulsante di registrazione
     */
    public HBox createRegisterBox(Button registerButton){
        HBox registerBox = new HBox();
        registerBox.setStyle(
                "-fx-padding: 10;"+
                "-fx-alignment: TOP_RIGHT;"
        );
        // Compone il layout con i rispettivi bottoni.
        registerBox.getChildren().addAll(registerButton);
        return registerBox;
    }
}

