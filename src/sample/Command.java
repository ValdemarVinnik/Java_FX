package sample;

import javax.print.DocFlavor;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Command {
    AUTH("/auth"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            return new String[]{ split[1],split[2]};
        }
    },

    REG("/reg"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            return new String[]{split[1],split[2],split[3]};
        }
    },

    AUTHOK("/authok"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            return new String[]{split[1]};
        }
    },

    REGOK("/regok"){
         @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            return new String[]{split[1]};
        }
    },

    FOR_SAVE("/for_save"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER, 2);

            return new String[]{split[1]};
        }
    },

    HISTORY("/history"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER, 2);

            return new String[]{split[1]};
        }
    },

    END("/end"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            return new String[]{split[0]};

        }
    },

    PRIVATE_MESSAGE("/w"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER,3);
            return new String[]{split[1],split[2]};
        }
    },

    ERROR("/error"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER, 2);
            return new String[]{split[1]};
        }
    },

    CLIENTS("/clients"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER);
            String[] nicks = new String[split.length - 1];
            for (int i = 0; i < nicks.length; i++) {
                nicks[i] = split[i + 1];
            }
            return nicks;
        }
    },

    MESSAGE("/message"){
        @Override
        public String[] parse(String commandText) {
            String[] split = commandText.split(TOKEN_DELIMITER, 2);
            return new String[]{split[1]};
        }
    };

    private final String command;
    static final String TOKEN_DELIMITER = "\\p{Blank}+";
    static final Map<String, Command> commandMap = Arrays.stream(values())
                            .collect(Collectors.toMap(Command::getCommand, Function.identity()));

    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static  boolean isCommand(String message){

        return message.startsWith("/");
    }

    public static Command getCommand(String message){

        if (!isCommand(message)){
            throw  new RuntimeException("'"+ message +"' is not command");
        }
        String cmd = message.split(TOKEN_DELIMITER, 2)[0];
        Command command = commandMap.get(cmd);

        if (command == null){
            throw new RuntimeException("Unknown command '"+ cmd +"'");
        }
        return command;
    }

    public abstract String[] parse(String command);

    public String collectMessage(String...params){
        return this.command + " "+ String.join(" ",params);
    }
}
