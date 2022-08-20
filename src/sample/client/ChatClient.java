package sample.client;

import javafx.application.Platform;
import sample.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static sample.Command.*;

public class ChatClient {
    private final Integer TIME_TO_AUTH = 15;
    private final ChatController controller;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ChatClient(ChatController controller) {
        this.controller = controller;
    }

    public void openConnection() throws IOException {
        socket = new Socket("localhost", 8888);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {

            try {
                if (waitAuth()) {
                    readMessages();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();
    }

    private boolean waitAuth() throws IOException {

        new Thread(() -> {
            int currentTime = TIME_TO_AUTH;
            try {
                while (currentTime > 0) {
                    Thread.sleep(1000);
                    controller.displayCurrentTime(currentTime);
                    currentTime--;

                    if(!controller.authBoxIsVisible()){
                        currentTime++;
                    }
                }

                if (!controller.isAuthSuccess()) {
                    sendMessage(END);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        while (true) {
            final String message = in.readUTF();

            Command command = Command.getCommand(message);
            String[] params = command.parse(message);

            if (command == Command.AUTHOK) {
                String nick = params[0];
                //controller.setAuth(true);
                controller.setMessage(true);
                controller.addMessage("Успешная авторизация под ником " + nick);
                return true;
            }

            if (command == Command.ERROR) {
                Platform.runLater(() -> controller.showError(params[0]));
                continue;
            }

            if (command == END) {
                return false;
            }
        }
    }

    private void closeConnection() {

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Runtime.getRuntime().exit(0);
    }

    private void readMessages() throws IOException {

        while (true) {
            final String message = in.readUTF();
            Command command = Command.getCommand(message);

            if (END == command) {
                controller.setMessage(false);
                break;
            }

            String[] params = command.parse(message);
            if (ERROR == command) {
                String messageError = params[0];
                Platform.runLater(() -> controller.showError(messageError));
                continue;
            }

            if (Command.MESSAGE == command) {
                Platform.runLater(() -> controller.addMessage(command.parse(message)[0]));
            }

            if (CLIENTS == command) {
                Platform.runLater(() -> controller.updateClientList(params));
            }
        }
    }

    private void sendMessage(String message) {

        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Command command, String... params) {
        sendMessage(command.collectMessage(params));
    }
}