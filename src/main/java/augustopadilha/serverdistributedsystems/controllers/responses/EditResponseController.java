package augustopadilha.serverdistributedsystems.controllers.responses;

import augustopadilha.serverdistributedsystems.controllers.system.DatabaseController;
import augustopadilha.serverdistributedsystems.models.UserModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.ResponseController.sendResponse;

public class EditResponseController {
    public static void send(String action, boolean error, String message, UserModel user, Socket socket) throws IOException {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapEdit = new HashMap<>();
        jsonMapEdit.put("action", action);
        jsonMapEdit.put("error", error);
        jsonMapEdit.put("message", message);

        if (user != null) {
            DatabaseController databaseController = new DatabaseController();
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", databaseController.getIdByEmail(user.getEmail()));
            dataMap.put("name", user.getName());
            dataMap.put("type", user.getType());
            dataMap.put("email", user.getEmail());
            jsonMapEdit.put("data", dataMap);
        }

        // Enviar o JSON para o cliente
        PrintWriter outToClientEdit = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseEdit = objectMapper.writeValueAsString(jsonMapEdit);
        sendResponse(outToClientEdit, jsonResponseEdit);
    }

}
