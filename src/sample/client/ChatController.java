package sample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sample.Command;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ChatController {

    @FXML
    private TextField timeForAuth;

    @FXML
    private ListView<String> clientList;
    @FXML
    private HBox authBox;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passField;

    @FXML
    private HBox messageBox;

    @FXML
    private TextField messageField;

    @FXML
    private TextArea messageArea;

    private final ChatClient client;
    private String selectedNick;

    public ChatController() {

        this.client = new ChatClient(this);

        while (true) {
            try {
                client.openConnection();
                break;
            } catch (IOException e) {
                showNotification();
            }
        }
    }

    private void showNotification() {
        final Alert alert = new Alert(Alert.AlertType.ERROR,
                "Не могу подключится к серверу.\n" +
                        "Проверте, что сервер запущен и доступен",
                new ButtonType("Попробовать снова", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Выйти", ButtonBar.ButtonData.CANCEL_CLOSE));

        alert.setTitle("Ошибка подключения!");
        final Optional<ButtonType> answer = alert.showAndWait();
        final boolean isExist = answer.map(select -> select.getButtonData().isCancelButton()).orElse(false);
        if (isExist) {
            System.exit(0);
        }
    }

    public void clickSendButton() {
        String message = messageField.getText();

        if (message.trim().isEmpty()) {
            return;
        }

        if (selectedNick != null){
            client.sendMessage(Command.PRIVATE_MESSAGE,selectedNick, message);
            selectedNick = null;
        } else {
            client.sendMessage(Command.MESSAGE, message);
        }

        messageField.clear();
        messageField.requestFocus();
    }

    public void addMessage(String message) {
        messageArea.appendText(message + "\n");
    }

    public void setAuth(boolean success) {
        authBox.setVisible(!success);
        messageBox.setVisible(success);

    }

    public void signinBtnClick() {

        client.sendMessage(Command.AUTH, loginField.getText(), passField.getText());
    }

    public void showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage,
                new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        alert.setTitle("Error");
        alert.showAndWait();
    }

    public void selectClients(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            String selectedNick = clientList.getSelectionModel().getSelectedItem();
            if (selectedNick != null && !selectedNick.isEmpty()){
                this.selectedNick = selectedNick;
            }
        }

    }

    public void updateClientList(String[] clients) {
        clientList.getItems().clear();
        clientList.getItems().addAll(clients);
    }

    public void signOutClick(ActionEvent actionEvent) {
        client.sendMessage(Command.END);
    }

    public ChatClient getClient() {
        return client;
    }

    public boolean isAuthSuccess() {
        return messageBox.isVisible();
    }

    public void displayCurrentTime(int currentTime) {
        timeForAuth.clear();
        timeForAuth.setText(""+currentTime);
    }
}

