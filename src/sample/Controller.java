package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private Button button;

    @FXML
    private TextArea chatsDisplay;

    @FXML
    private TextField userAnswer;

    public void clickSendButton(ActionEvent actionEvent) {
        String message = userAnswer.getText();

        if (message.trim().isEmpty()){
            return;
        }

        chatsDisplay.appendText(message +"\n");
        userAnswer.clear();
        userAnswer.requestFocus();
    }
}
