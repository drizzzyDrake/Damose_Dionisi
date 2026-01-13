package view.nodes;

// Classi JavaFX necessarie per la creazione dei nodi.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Classe node per la zona di registrazione.
 * <p>
 * Fornisce i componenti grafici per la zona di registrazione.
 * </p>
 */
public class RegisterNode {

    private final LoginNode loginNode = new LoginNode();

    /**
     * Crea il campo di testo per l'username.
     *
     * @return TextField per inserire l'username
     */
    public TextField createUsernameField() {
        return loginNode.createUsernameField();
    }

    /**
     * Crea il campo password.
     *
     * @return PasswordField per inserire la password
     */
    public PasswordField createPasswordField() {
        return loginNode.createPasswordField();
    }

    /**
     * Crea il campo per la password visibile.
     *
     * @return TextField per mostrare la password in chiaro
     */
    public TextField createVisiblePasswordField() {
        return loginNode.createVisiblePasswordField();
    }

    /**
     * Crea l'etichetta per i messaggi informativi o di errore.
     *
     * @return Label per visualizzare messaggi
     */
    public Label createMessageLabel() {
        return loginNode.createMessageLabel();
    }

    /**
     * Crea il bottone per mostrare/nascondere la password.
     *
     * @return Button con icona per la visione della password
     */
    public Button createVisiblePasswordButton() {
        return loginNode.createVisiblePasswordButton();
    }

    /**
     * Crea il bottone per registrarsi.
     *
     * @return Button per effettuare la registrazione
     */
    public Button createRegisterButton() {
        Button registerButton = new Button("Registrati");
        registerButton.setStyle(
                "-fx-background-color: #ffa31a;"+
                        "-fx-text-fill: black;"+
                        "-fx-font-weight: bold;"+
                        "-fx-background-radius: 15;"+
                        "-fx-cursor: hand;"+
                        "-fx-font-size: 16;"
        );
        return registerButton;
    }

    /**
     * Crea il bottone annulla.
     *
     * @return Button per annullare la registrazione
     */
    public Button createCancelButton() {
        Button cancelButton = new Button("Annulla");
        cancelButton.setStyle(
                "-fx-background-color: BLACK;"+
                        "-fx-text-fill: white;"+
                        "-fx-background-radius: 10;"+
                        "-fx-cursor: hand;"+
                        "-fx-font-size: 16;"
        );
        return cancelButton;
    }
}

