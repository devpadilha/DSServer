package augustopadilha.serverdistributedsystems.controllers.responses.users;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class LoginResponseController {
    public static void send(String action, boolean error, String message, Socket socket, String token) throws IOException {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapLogin = new HashMap<>();
        jsonMapLogin.put("action", action);
        jsonMapLogin.put("error", error);
        jsonMapLogin.put("message", message);

        Map<String, String> dataMapLogin = new HashMap<>();
        dataMapLogin.put("token", token);
        jsonMapLogin.put("data", dataMapLogin);

        // Enviar o JSON para o cliente
        PrintWriter outToClientLogin = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseLogin = objectMapper.writeValueAsString(jsonMapLogin);
        sendResponse(outToClientLogin, jsonResponseLogin);
    }
}
