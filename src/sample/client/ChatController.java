package sample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import sample.Command;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.Optional;

import static sample.Command.FOR_SAVE;

public class ChatController {
    @FXML
    public HBox regBox;

    @FXML
    public TextField newUserNickField;

    @FXML
    public TextField newUserLoginField;

    @FXML
    public PasswordField newUserPassField;

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
        //setMessage(false);
        // setAuth(true);

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

        if (selectedNick != null) {
            client.sendMessage(Command.PRIVATE_MESSAGE, selectedNick, message);
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

    public void setMessageWindow(boolean success) {
        messageBox.setVisible(success);

    }

    public void setAuthWindow(boolean success) {
        authBox.setVisible(success);
    }

    public boolean authBoxIsVisible() {
        return authBox.isVisible();
    }

    public void setRegWindow(boolean success) {
        authBox.setVisible(!success);
        regBox.setVisible(success);
    }

    public void signinBtnClick() {
        client.sendMessage(Command.AUTH, loginField.getText(), passField.getText());
    }

    public void regNewUserBtnClick(ActionEvent actionEvent) {
        client.sendMessage(Command.REG, newUserNickField.getText(),
                            newUserLoginField.getText(), newUserPassField.getText());
    }

    public void showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage,
                new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        alert.setTitle("Error");
        alert.showAndWait();
    }

    public void showInformation(String informationMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, informationMessage,
                new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        alert.setTitle("Information");
        alert.showAndWait();
    }


    public void selectClients(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            String selectedNick = clientList.getSelectionModel().getSelectedItem();
            if (selectedNick != null && !selectedNick.isEmpty()) {
                this.selectedNick = selectedNick;
            }
        }

    }

    public String glueText(String[] array, int startIndex){
        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < array.length; i++){
            builder.append(array[i]+"\n");
        }
        return builder.toString();
    }

    public String getTextFromDisplay(){

        String[] array= messageArea.getText().split("\n");
        if(array.length < 100){
           return glueText(array,1);
        }else{
            return glueText(array, array.length - 100);
        }
    }

    public void updateClientList(String[] clients) {
        clientList.getItems().clear();
        clientList.getItems().addAll(clients);
    }

    private void sendHistory(){
        client. sendMessage(FOR_SAVE,getTextFromDisplay());
    }

    public void signOutClick(ActionEvent actionEvent) {
        sendHistory();
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
        timeForAuth.setText("" + currentTime);
    }

    public void regBtnClick(ActionEvent actionEvent) {
        setRegWindow(true);
    }


}

