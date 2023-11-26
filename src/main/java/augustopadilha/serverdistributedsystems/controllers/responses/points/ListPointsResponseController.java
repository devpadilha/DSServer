package augustopadilha.serverdistributedsystems.controllers.responses.points;

import augustopadilha.serverdistributedsystems.models.Point;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class ListPointsResponseController {
    public static void send(String action, boolean error, String message, List<Point> points, Socket socket) throws Exception {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapList = new HashMap<>();
        jsonMapList.put("action", action);
        jsonMapList.put("error", error);
        jsonMapList.put("message", message);

        if (!error) {
            List<Map<String, Object>> pointsList = new ArrayList<>();
            for (Point point : points) {
                Map<String, Object> pointsMap = new HashMap<>();
                pointsMap.put("id", point.getId());
                pointsMap.put("name", point.getName());
                pointsMap.put("obs", point.getObs());
                pointsList.add(pointsMap);
            }

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("pontos", pointsList);
            jsonMapList.put("data", dataMap);
        }

        // Enviar o JSON para o cliente
        PrintWriter outToClientList = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseList = objectMapper.writeValueAsString(jsonMapList);
        sendResponse(outToClientList, jsonResponseList);
    }
}
