package augustopadilha.serverdistributedsystems.controllers.responses.segments;

import augustopadilha.serverdistributedsystems.controllers.system.DatabaseController;
import augustopadilha.serverdistributedsystems.models.Point;
import augustopadilha.serverdistributedsystems.models.Segment;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class EditSegmentResponseController {
    public static void send(String action, boolean error, String message, Segment segment, Socket socket) throws IOException {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapEdit = new HashMap<>();
        jsonMapEdit.put("action", action);
        jsonMapEdit.put("error", error);
        jsonMapEdit.put("message", message);

        if (segment != null) {
            DatabaseController databaseController = new DatabaseController();

            Map<String, Object> segmentMap = new HashMap<>();
            segmentMap.put("id", databaseController.getSegmentById(segment.getId()));

            Map<String, Object> originPointMap = new HashMap<>();
            originPointMap.put("id", databaseController.getPointById(segment.getOriginPoint()).getId());
            originPointMap.put("name", databaseController.getPointById(segment.getOriginPoint()).getName());
            originPointMap.put("obs", databaseController.getPointById(segment.getOriginPoint()).getObs());
            segmentMap.put("ponto_origem", databaseController.getPointById(segment.getOriginPoint()));

            Map<String, Object> destinyPointMap = new HashMap<>();
            destinyPointMap.put("id", databaseController.getPointById(segment.getDestinyPoint()).getId());
            destinyPointMap.put("name", databaseController.getPointById(segment.getDestinyPoint()).getName());
            destinyPointMap.put("obs", databaseController.getPointById(segment.getDestinyPoint()).getObs());
            segmentMap.put("ponto_destino", databaseController.getPointById(segment.getDestinyPoint()));

            segmentMap.put("direcao", segment.getDirection());
            segmentMap.put("distancia", segment.getDistance());
            segmentMap.put("obs", segment.getObs());

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("segmento", segmentMap);
            jsonMapEdit.put("data", dataMap);
        }

        // Enviar o JSON para o cliente
        PrintWriter outToClientEdit = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseEdit = objectMapper.writeValueAsString(jsonMapEdit);
        sendResponse(outToClientEdit, jsonResponseEdit);
    }
}
