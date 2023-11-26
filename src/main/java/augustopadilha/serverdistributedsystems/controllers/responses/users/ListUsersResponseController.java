package augustopadilha.serverdistributedsystems.controllers.responses.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import augustopadilha.serverdistributedsystems.models.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static augustopadilha.serverdistributedsystems.controllers.responses.common.ResponseController.sendResponse;

public class ListUsersResponseController {
    public static void send(String action, boolean error, String message, List<User> users, Socket socket) throws IOException {
        // Criar o JSON de resposta
        Map<String, Object> jsonMapList = new HashMap<>();
        jsonMapList.put("action", action);
        jsonMapList.put("error", error);
        jsonMapList.put("message", message);

        if (!error) {
            List<Map<String, Object>> userList = new ArrayList<>();
            for (User user : users) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("name", user.getName());
                userMap.put("type", user.getType());
                userMap.put("email", user.getEmail());
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
