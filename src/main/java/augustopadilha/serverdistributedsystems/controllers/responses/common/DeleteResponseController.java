package augustopadilha.serverdistributedsystems.controllers.responses.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class DeleteResponseController {
    public static void send(String action, boolean error, String message, Socket socket) throws IOException {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapLogin = new HashMap<>();
        jsonMapLogin.put("action", action);
        jsonMapLogin.put("error", error);
        jsonMapLogin.put("message", message);

        // Enviar o JSON para o cliente
        PrintWriter outToClientLogin = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseLogin = objectMapper.writeValueAsString(jsonMapLogin);
        sendResponse(outToClientLogin, jsonResponseLogin);
    }
}
