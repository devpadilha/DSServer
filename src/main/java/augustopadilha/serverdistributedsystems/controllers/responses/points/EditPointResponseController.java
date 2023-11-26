package augustopadilha.serverdistributedsystems.controllers.responses.points;

import augustopadilha.serverdistributedsystems.controllers.system.DatabaseController;
import augustopadilha.serverdistributedsystems.models.Point;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class EditPointResponseController {
    public static void send(String action, boolean error, String message, Point point, Socket socket) throws IOException {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapEdit = new HashMap<>();
        jsonMapEdit.put("action", action);
        jsonMapEdit.put("error", error);
        jsonMapEdit.put("message", message);

        if (point != null) {
            DatabaseController databaseController = new DatabaseController();
            Map<String, Object> dataMap = new HashMap<>();
            Map<String, Object> pointMap = new HashMap<>();
            pointMap.put("id", databaseController.getPointById(point.getId()));
            pointMap.put("name", point.getName());
            pointMap.put("obs", point.getObs());
            dataMap.put("ponto", pointMap);
            jsonMapEdit.put("data", dataMap);
        }

        // Enviar o JSON para o cliente
        PrintWriter outToClientEdit = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseEdit = objectMapper.writeValueAsString(jsonMapEdit);
        sendResponse(outToClientEdit, jsonResponseEdit);
    }
}
