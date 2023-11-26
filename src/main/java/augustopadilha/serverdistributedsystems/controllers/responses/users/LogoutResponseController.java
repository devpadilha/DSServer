package augustopadilha.serverdistributedsystems.controllers.responses.users;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class LogoutResponseController {
    public static void send(String action, boolean error, String message, Socket socket) throws IOException {
        // Criar o JSON para o logout
        Map<String, Object> jsonMapLogout = new HashMap<>();
        jsonMapLogout.put("action", action);
        jsonMapLogout.put("error", error);
        jsonMapLogout.put("message", message);

        // Enviar o JSON para o cliente
        PrintWriter outToClientLogout = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseLogout = objectMapper.writeValueAsString(jsonMapLogout);
        sendResponse(outToClientLogout, jsonResponseLogout);
    }
}
