package sample.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatServer {

    private final Map<String, ClientHandler> clients;
    private static Logger log = LoggerFactory.getLogger("file");

    public ChatServer() {
        this.clients = new HashMap<>();

    }

    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(8888);
             AuthService authService = new InMemoryAuthService()) {

            log.info(String.format("Сервер запустился"));

            while (true) {

                log.info(String.format("Ожидается подключение клиента"));

                final Socket socket = serverSocket.accept();

                log.info(String.format("Клиент подключён"));

                new ClientHandler(socket, this, authService);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler client) {
        clients.put(client.getNick(), client);
        broadcastClientsList();
    }

    private void broadcastClientsList() {
        String nicks = clients.values()
                .stream()
                .map(ClientHandler::getNick)
                .collect(Collectors.joining(" "));
        broadcast(Command.CLIENTS, nicks);
    }

    public void broadcast(Command command, String message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(command, message);
        }
    }

    public boolean isNickBusy(String nick) {
        return clients.get(nick) != null;

    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client.getNick());
        broadcastClientsList();
    }

    public void sendPrivateMessage(ClientHandler from, String nickTo, String message) {
        ClientHandler clientTo = clients.get(nickTo);
        if (clientTo == null) {
            from.sendMessage(Command.ERROR, "пользователь не авторизован...");
            log.error(String.format("Попытка послать сообщение не авторизованному пользователю %s",nickTo));
            return;
        }

        clientTo.sendMessage(Command.MESSAGE, "От " + from.getNick() + ": " + message);
        from.sendMessage(Command.MESSAGE, "Учаснику " + nickTo + ": " + message);
        log.info(String.format("Пользователь %s послал личное сообщение %s",from.getNick(),nickTo));
    }
}
