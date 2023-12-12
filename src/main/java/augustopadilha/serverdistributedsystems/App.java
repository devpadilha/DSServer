package augustopadilha.serverdistributedsystems;

import augustopadilha.serverdistributedsystems.system.connection.Server;
import augustopadilha.serverdistributedsystems.views.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        openConnectWindow();
    }

    public static void openConnectWindow() {
        ViewFactory.getInstance().showConnectWindow();
    }

    public void startServer(String port) {
        Thread serverThread = new Thread(new Server(Integer.parseInt(port)));
        serverThread.start();
    }
}