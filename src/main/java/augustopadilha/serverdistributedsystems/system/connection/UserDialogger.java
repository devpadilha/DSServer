package augustopadilha.serverdistributedsystems.system.connection;

import augustopadilha.serverdistributedsystems.views.ViewFactory;
import augustopadilha.serverdistributedsystems.system.connection.receive.UserDialogActions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserDialogger implements Runnable {
    private static UserDialogger userdialogger = null;
    private final Socket clientSocket;
    private final ObjectMapper objectMapper;

    public UserDialogger(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.objectMapper = new ObjectMapper();
    }

    public synchronized UserDialogger getInstance() {
        if (userdialogger == null) {
            userdialogger = new UserDialogger(this.clientSocket);
        }
        return userdialogger;
    }

    @Override
    public void run() {
        try {
            SessionManager.getInstance().addSession(clientSocket.getInetAddress().toString());
            UserDialogActions.handleClientRequest(clientSocket);
        } catch (IOException e) {
            try {
                SessionManager.getInstance().removeSessions(clientSocket.getInetAddress().toString());
                System.out.println("Socket closed!");
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
