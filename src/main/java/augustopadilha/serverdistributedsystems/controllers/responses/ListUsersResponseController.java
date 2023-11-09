package augustopadilha.serverdistributedsystems.controllers.responses;

import com.fasterxml.jackson.databind.ObjectMapper;
import augustopadilha.serverdistributedsystems.models.UserModel;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.ResponseController.sendResponse;

public class ListUsersResponseController {
    public static void send(String action, boolean error, String message, List<UserModel> userModels, Socket socket) throws IOException {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapList = new HashMap<>();
        jsonMapList.put("action", action);
        jsonMapList.put("error", error);
        jsonMapList.put("message", message);

        if (!error) {
            List<Map<String, Object>> userList = new ArrayList<>();
            for (UserModel userModel : userModels) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", userModels.indexOf(userModel) + 1);
                userMap.put("name", userModel.getName());
                userMap.put("type", userModel.getType());
                userMap.put("email", userModel.getEmail());
                userList.add(userMap);
            }

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("users", userList);
            jsonMapList.put("data", dataMap);
        }

        // Enviar o JSON para o cliente
        PrintWriter outToClientList = new PrintWriter(socket.getOutputStream(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponseList = objectMapper.writeValueAsString(jsonMapList);
        sendResponse(outToClientList, jsonResponseList);
    }
}
