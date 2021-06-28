package application;

import configuration.Database;
import javafx.stage.Stage;

public class State {
    public static Database database = Database.getInstance();
    public static String lobbycode;
    public static int TurnID;
    public static Stage stage;

}
