package sample.server;

import sample.Model.User;
import sample.db.DBExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthService implements AuthService {

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        User user = new DBExecutor().getUserByLoginAndPassword(login, password);

        System.out.println("InMemoryAutService.getNick..." + user.getNick());
        return user.getNick();
    }

    @Override
    public Boolean registrationNewUser(String nick, String login, String password) {
        return new DBExecutor().registrationNewUser(nick, login, password);
    }

    @Override
    public void close() throws IOException {
        System.out.println("Сервис аутентификации остановлен");
    }
}
