package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Classe node per la finestra di login.
 * <p>
 * Fornisce i componenti grafici per la finestra di login.
 * </p>
 */
public class LoginNode {

    /**
     * Crea il campo di testo per l'username.
     *
     * @return TextField per inserire l'username
     */
    public TextField createUsernameField() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle(
                "-fx-font-size: 14;"+
                        "-fx-background-radius: 15;"
        );
        return usernameField;
    }

    /**
     * Crea il campo password.
     *
     * @return PasswordField per inserire la password
     */
    public PasswordField createPasswordField() {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(
                "-fx-font-size: 14;"+
                        "-fx-background-radius: 15;"
        );
        return passwordField;
    }

    /**
     * Crea il campo per la password visibile.
     *
     * @return TextField per mostrare la password in chiaro
     */
    public TextField createVisiblePasswordField() {
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setStyle(
                "-fx-font-size: 14;"+
                        "-fx-background-radius: 15;"
        );
        visiblePasswordField.setVisible(false);
        return visiblePasswordField;
    }

    /**
     * Crea il bottone per mostrare/nascondere la password.
     *
     * @return Button con icona dell'occhio
     */
    public Button createVisiblePasswordButton() {
        Button visiblePasswordButton = new Button();
        FontIcon eyeIcon = new FontIcon(FontAwesomeSolid.EYE);
        eyeIcon.setIconColor(Color.GRAY);
        visiblePasswordButton.setGraphic(eyeIcon);
        visiblePasswordButton.setStyle(
                "-fx-background-color: TRANSPARENT;"
        );
        return visiblePasswordButton;
    }

    /**
     * Crea l'etichetta per i messaggi di login.
     *
     * @return Label per visualizzare messaggi informativi o di errore
     */
    public Label createMessageLabel() {
        Label message = new Label();
        message.setStyle(
                "-fx-font-size: 15;"+
                        "-fx-text-fill: #ffffff;"+
                        "-fx-padding: 5;"
        );
        return message;
    }

    /**
     * Crea il bottone per effettuare il login.
     *
     * @param usernameField Campo username (non modificato all'interno del metodo)
     * @param passwordField Campo password (non modificato all'interno del metodo)
     * @param message       Label dei messaggi (non modificata all'interno del metodo)
     * @return              Button per il login
     */
    public Button createLoginButton(TextField usernameField,
                                    PasswordField passwordField,
                                    Label message) {
        Button loginButton = new Button("Login");
        loginButton.setStyle(
                "-fx-background-radius: 15;"+
                        "-fx-font-size: 18;"+
                        "-fx-text-fill: #000000;"+
                        "-fx-underline: false;"+
                        "-fx-cursor: hand;"+
                        "-fx-background-color: #ffa31a;"+
                        "-fx-font-weight: bold;"
        );
        return loginButton;
    }

    /**
     * Crea il bottone per la registrazione.
     *
     * @param usernameField Campo username (non modificato all'interno del metodo)
     * @param passwordField Campo password (non modificato all'interno del metodo)
     * @param message       Label dei messaggi (non modificata all'interno del metodo)
     * @return              Button per registrarsi
     */
    public Button createRegisterButton(TextField usernameField,
                                       PasswordField passwordField,
                                       Label message) {
        Button registerButton = new Button("Registrati");
        registerButton.setStyle(
                "-fx-font-size: 17;"+
                        "-fx-text-fill: #ffa31a;"+
                        "-fx-cursor: hand;"+
                        "-fx-background-color: TRANSPARENT;"+
                        "-fx-border-color: #ffa31a;"+
                        "-fx-border-radius: 15;"
        );
        return registerButton;
    }
}

