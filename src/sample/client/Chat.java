package sample.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Command;

public class Chat extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(Chat.class.getResource("chat-view.fxml"));
        primaryStage.setTitle("My chat");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 600, 400));
        primaryStage.show();

        ChatController controller = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> controller.getClient().sendMessage(Command.END));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
