package augustopadilha.serverdistributedsystems.controllers.responses.segments;

import augustopadilha.serverdistributedsystems.controllers.system.DatabaseController;
import augustopadilha.serverdistributedsystems.models.Point;
import augustopadilha.serverdistributedsystems.models.Segment;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class ListSegmentsResponseSender {
    public static void send(String action, boolean error, String message, List<Segment> segments, Socket socket) throws Exception {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapList = new HashMap<>();
        jsonMapList.put("action", action);
        jsonMapList.put("error", error);
        jsonMapList.put("message", message);

        if (!error) {
            List<Map<String, Object>> segmentsList = new ArrayList<>();
            DatabaseController databaseController = new DatabaseController();
            for (Segment segment : segments) {
                Map<String, Object> segmentsMap = new HashMap<>();
                segmentsMap.put("id", segment.getId());

                Map<String, Object> originPointMap = new HashMap<>();
                originPointMap.put("id", databaseController.getPointById(segment.getOriginPoint()).getId());
                originPointMap.put("name", databaseController.getPointById(segment.getOriginPoint()).getName());
                originPointMap.put("obs", databaseController.getPointById(segment.getOriginPoint()).getObs());
                segmentsMap.put("ponto_origem", originPointMap);

                Map<String, Object> destinyPointMap = new HashMap<>();
                destinyPointMap.put("id", databaseController.getPointById(segment.getDestinyPoint()).getId());
                destinyPointMap.put("name", databaseController.getPointById(segment.getDestinyPoint()).getName());
                destinyPointMap.put("obs", databaseController.getPointById(segment.getDestinyPoint()).getObs());
                segmentsMap.put("ponto_destino", destinyPointMap);

                segmentsMap.put("direcao", segment.getDirection());
                segmentsMap.put("distancia", segment.getDistance());
                segmentsMap.put("obs", segment.getObs());

                segmentsList.add(segmentsMap);
            }

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("segmentos", segmentsList);
            jsonMapList.put("data", dataMap);
        }

        // Enviar o JSON para o cliente
        PrintWriter outToClientList = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseList = objectMapper.writeValueAsString(jsonMapList);
        sendResponse(outToClientList, jsonResponseList);
    }
}
