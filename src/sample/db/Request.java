package sample.db;

public class Request {

    static String selectByLogin(String login){
        return String.format("select * from users where login = '%s'",login);
    }

    static String selectByLoginAndPassword(String login, String password){
        return String.format("select * from users where login = '%s' AND password = '%s'",login,password);
    }

    static String selectByNick(String nick){
        return String.format("select * from users where nick = '%s'",nick);
    }

    static String insertNewUser(String nick, String login, String password){
        return String.format("INSERT INTO users (nick, login, password) VALUES ('%s','%s','%s')",
                            nick, login, password);

    }
}
