package sample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class ChatController {

    @FXML
    private HBox authBox;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passField;

    @FXML
    private VBox messageBox;

    @FXML
    private TextField messageField;

    @FXML
    private TextArea messageArea;

    @FXML
    private Button button;

    private final ChatClient client;

    public ChatController() {

        this.client = new ChatClient(this);

        while (true){
        try {
            client.openConnection();
            break;
        } catch (IOException e) {
            showNotification();
        }
        }
    }

    private void showNotification() {
        final  Alert alert = new Alert(Alert.AlertType.ERROR,
                "Не могу подключится к серверу.\n" +
                        "Проверте, что сервер запущен и доступен",
                new ButtonType("Попробовать снова", ButtonBar.ButtonData.OK_DONE ),
                new ButtonType("Выйти", ButtonBar.ButtonData.CANCEL_CLOSE ));

        alert.setTitle("Ошибка подключения!");
        final Optional<ButtonType> answer = alert.showAndWait();
        final boolean isExist = answer.map(select -> select.getButtonData().isCancelButton()).orElse(false);
        if (isExist){
            System.exit(0);
        }
    }

    public void clickSendButton() {
        String message = messageField.getText();

        if (message.trim().isEmpty()) {
            return;
        }

        client.sendMessage(message);
        messageField.clear();
        messageField.requestFocus();
    }

    public void addMessage(String message) {
        messageArea.appendText(message + "\n");
    }
    public void setAuth(boolean success){
        authBox.setVisible(!success);
        messageBox.setVisible(success);
    }

    public void signinBtnClick() {
        client.sendMessage("/auth " + loginField.getText()
                + " " + passField.getText());
    }
}
