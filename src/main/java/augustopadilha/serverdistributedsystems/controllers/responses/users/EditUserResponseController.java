package augustopadilha.serverdistributedsystems.controllers.responses.users;

import augustopadilha.serverdistributedsystems.controllers.system.DatabaseController;
import augustopadilha.serverdistributedsystems.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class EditUserResponseController {
    public static void send(String action, boolean error, String message, User user, Socket socket) throws IOException {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapEdit = new HashMap<>();
        jsonMapEdit.put("action", action);
        jsonMapEdit.put("error", error);
        jsonMapEdit.put("message", message);

        if (user != null) {
            DatabaseController databaseController = new DatabaseController();
            Map<String, Object> dataMap = new HashMap<>();
            Map<String, Object> pointrMap = new HashMap<>();
            pointrMap.put("id", databaseController.getIdByEmail(user.getEmail()));
            pointrMap.put("name", user.getName());
            pointrMap.put("type", user.getType());
            pointrMap.put("email", user.getEmail());
            dataMap.put("ponto", pointrMap);
            jsonMapEdit.put("data", dataMap);
        }

        // Enviar o JSON para o cliente
        PrintWriter outToClientEdit = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseEdit = objectMapper.writeValueAsString(jsonMapEdit);
        sendResponse(outToClientEdit, jsonResponseEdit);
    }
}
