package augustopadilha.serverdistributedsystems.controllers.responses;

import augustopadilha.serverdistributedsystems.controllers.system.DatabaseController;
import augustopadilha.serverdistributedsystems.controllers.system.SessionController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import augustopadilha.serverdistributedsystems.models.UserModel;
import static augustopadilha.serverdistributedsystems.controllers.responses.ResponseController.sendResponse;

public class ListDataResponseController {
    public static void send(String action, boolean error, String message, String token, Socket socket) throws IOException {

        // Obter usuário da sessão
        UserModel user = SessionController.getSessionUser(token);

        // Criar o JSON de resposta
        Map<String, Object> jsonMapData = new HashMap<>();
        jsonMapData.put("action", action);
        jsonMapData.put("error", error);
        jsonMapData.put("message", message);

        if (!error) {
            DatabaseController databaseController = new DatabaseController();
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", databaseController.getIdByEmail(user.getEmail()));
            dataMap.put("name", user.getName());
            dataMap.put("type", user.getType());
            dataMap.put("email", user.getEmail());

            jsonMapData.put("data", dataMap);
        } else jsonMapData.put("data", null);

        // Enviar o JSON para o cliente
        PrintWriter outToClientListData = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseListData = objectMapper.writeValueAsString(jsonMapData);
        sendResponse(outToClientListData, jsonResponseListData);
    }
}
