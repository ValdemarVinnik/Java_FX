package sample.server;

import java.io.Closeable;

public interface AuthService extends Closeable {
    String getNickByLoginAndPassword(String login, String password);
    Boolean registrationNewUser(String nick, String login, String password);
}
