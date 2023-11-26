package augustopadilha.serverdistributedsystems.controllers.responses.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class RegisterResponseController {
    public static void send(String action, boolean error, String message, Socket socket) throws IOException {
            // Criar o JSON para o cadastro
            Map<String, Object> jsonMapCadastro = new HashMap<>();
            jsonMapCadastro.put("action", action);
            jsonMapCadastro.put("error", error);
            jsonMapCadastro.put("message", message);

            // Enviar o JSON para o cliente
            PrintWriter outToClientCadastro = new PrintWriter(socket.getOutputStream(), true);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponseCadastro = objectMapper.writeValueAsString(jsonMapCadastro);
            sendResponse(outToClientCadastro, jsonResponseCadastro);

    }
}
