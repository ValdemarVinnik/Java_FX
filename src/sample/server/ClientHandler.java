package sample.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSOutput;
import sample.Command;

import javax.print.DocFlavor;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientHandler {

    private Socket socket;
    private ChatServer server;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;
    private AuthService authService;

    private String pathToFile = String.format("src/sample/server/usersHistory/%s", getNick());
    private Path file;

    private static Logger log = LoggerFactory.getLogger("file");

    public ClientHandler(Socket socket, ChatServer server, AuthService authService) {


        try {
            this.server = server;
            this.socket = socket;
            this.authService = authService;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {

                try {
                    if (authenticate()) {
                        readMessage();
                    }
                } finally {
                    closeConnection();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean authenticate() {
        while (true) {
            try {
                final String message = in.readUTF();
                if (Command.isCommand(message)) {
                    Command command = Command.getCommand(message);

                    if (command == Command.END) {
                        if (nick != null) {
                            log.info(String.format("Соединение с %s прервано...", getNick()));
                        } else {
                            log.info(String.format("Соединение с клиентом прервано..."));
                        }
                        return false;
                    }

                    if (command == Command.AUTH) {
                        String[] params = command.parse(message);
                        String login = params[0];
                        String password = params[1];
                        String nick = authService.getNickByLoginAndPassword(login, password);

                        if (nick == null) {
                            sendMessage(Command.ERROR, "Неверные логин и пароль...");
                            log.error(String.format("Попытка ввода неверных логина %s или пороля %s..", login, password));
                        }

                        if (nick != null) {

                            if (server.isNickBusy(nick)) {
                                sendMessage(Command.ERROR, "Пользователь уже авторизован");
                                log.error(String.format("попытка зайти под занятым nick-ом %s...", nick));
                                continue;
                            }

                            this.nick = nick;

                            sendMessage(Command.AUTHOK, nick);
                            sendMessage(Command.HISTORY, readFromFile());

                            server.broadcast(Command.MESSAGE, "Пользователь " + nick + " зашёл в чат...");
                            server.subscribe(this);

                            log.info(String.format("Пользователь %s зашёл в чат...", nick));

                            return true;
                        }
                    }

                    if (command == Command.REG) {
                        String[] params = command.parse(message);
                        String nick = params[0];
                        String login = params[1];
                        String password = params[2];

                        if (authService.registrationNewUser(nick, login, password)) {
                            sendMessage(Command.REGOK, "Вы_зарегистрировались_под_" + nick);
                            log.info(String.format("nick %s зарегистрировался", nick));
                        } else {
                            sendMessage(Command.ERROR, "nick " + nick + " уже занят.");

                        }
                    }
                }
            } catch (IOException e) {
                log.error(e.toString());

                return false;
            }
        }
    }

    public void sendMessage(Command command, String... params) {
        sendMessage(command.collectMessage(params));
    }


    private void saveToFile(String data) {

        file = Path.of(pathToFile);
        try (FileWriter writer = new FileWriter(String.valueOf(file))) {
            writer.write(data);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String readFromFile() {

        pathToFile = String.format("src/sample/server/usersHistory/%s.txt", getNick());
        file = Path.of(pathToFile);

        if (Files.exists(file)) {

            try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(file)))) {
                StringBuilder messageFromFile = new StringBuilder();

                while (reader.ready()) {
                    messageFromFile.append(reader.readLine() + "\n");
                }

                return messageFromFile.toString();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return "";
    }

    private void closeConnection() {

        sendMessage(Command.END);

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        if (socket != null) {
            server.unsubscribe(this);
            try {
                socket.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        if (nick != null) {
            log.info(String.format("Соединение с %s прервано...", nick));
        } else {
            log.info(String.format("Соединение с клиентом прервано..."));
        }
    }

    private void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void readMessage() {
        while (true) {
            try {
                final String message = in.readUTF();
                Command command = Command.getCommand(message);
                if (command == Command.END) {
                    break;
                }

                if (command == Command.PRIVATE_MESSAGE) {
                    String[] params = command.parse(message);
                    server.sendPrivateMessage(this, params[0], params[1]);
                    continue;
                }

                if (command == Command.FOR_SAVE) {
                    String[] params = command.parse(message);
                    saveToFile(params[0]);

                    continue;
                }

                server.broadcast(Command.MESSAGE, nick + ": " + command.parse(message)[0]);

            } catch (IOException e) {
               log.error(e.getMessage());
            }
        }
    }

    public String getNick() {
        return nick;
    }
}