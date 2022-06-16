package sample.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private List<ClientHandler> clients;

    public ChatServer() {
        this.clients = new ArrayList<>();

    }

    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(8888);
             AuthService authService = new InMemoryAuthService()) {

            while (true) {
                System.out.println("Ожидаю подключения");
                final Socket socket = serverSocket.accept();
                System.out.println("Клиент подключён");

                new ClientHandler(socket, this, authService);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message) {

        System.out.println(message.startsWith("/w"));
        if (message.startsWith("/w")) {
            System.out.println("Это личное сообщение");
            String[] split = message.split("\\p{Blank}+");
            for (ClientHandler client : clients) {
                System.out.println("для "+ split[1]);
                if (split[1].equals(client.getNick())) {

                    String[] usefulMessage = new String[split.length - 2];
                    System.arraycopy(usefulMessage, 0, split, 2, split.length - 1);
                    client.sendMessage(usefulMessage.toString());
                    break;
                }
            }
        } else
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (nick.equals(client.getNick())) {
                return true;
            }
        }
        return false;
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }
}
