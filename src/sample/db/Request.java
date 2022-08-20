package sample.db;

public class Request {

    static String SelectByLogin(String login){
        return String.format("select * from users where login = '%s'",login);
    }

    static String SelectByLoginAndPassword(String login, String password){
        return String.format("select * from users where login = '%s' AND password = '%s'",login,password);
    }
}
