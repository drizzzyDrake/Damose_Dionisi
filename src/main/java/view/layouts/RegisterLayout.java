package view.layouts;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Classe layout per la zona di registrazione.
 * <p>
 * Gestisce la disposizione dei nodi della zona di registrazione,
 * con le barre per password visibili, conferma password
 * e i pulsanti di conferma e annulla.
 */
public class RegisterLayout {

    /**
     * Crea la root della finestra di registrazione.
     *
     * @param usernameField       Campo di testo per l'username
     * @param passwordPane        StackPane per la password
     * @param confirmPasswordPane StackPane per la conferma della password
     * @param message             Label per eventuali messaggi
     * @param registerBox         HBox contenente i pulsanti di registrazione e annulla
     * @return                    VBox root della finestra di registrazione
     */
    public VBox createRegisterRoot(TextField usernameField,
                                   StackPane passwordPane,
                                   StackPane confirmPasswordPane,
                                   Label message,
                                   HBox registerBox) {
        VBox registerRoot = new VBox(10);
        registerRoot.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.8);"+
                "-fx-padding: 20;"+
                "-fx-alignment: CENTER;"+
                "-fx-background-radius: 20;"
        );
        // Compone il layout con i rispettivi bottoni.
        registerRoot.getChildren().addAll(usernameField, passwordPane, confirmPasswordPane, message, registerBox);
        return registerRoot;
    }

    /**
     * Crea il layout per la barra della password visibile.
     *
     * @param passwordField         PasswordField principale
     * @param visiblePasswordField  TextField visibile per la password
     * @param visiblePasswordButton Bottone per mostrare/nascondere la password
     * @return                      StackPane contenente la barra della password
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
     * Crea il layout per la zona dei bottoni in basso.
     *
     * @param registerButton Bottone di registrazione
     * @param cancelButton   Bottone per annullare la registrazione
     * @return               HBox contenente i bottoni
     */
    public HBox createRegisterBox(Button registerButton,
                                  Button cancelButton) {
        HBox registerBox = new HBox(5);
        registerBox.setStyle(
                "-fx-padding: 10;"+
                "-fx-alignment: CENTER;"
        );
        // Compone il layout con i rispettivi bottoni.
        registerBox.getChildren().addAll(registerButton, cancelButton);
        return registerBox;
    }
}

