package augustopadilha.serverdistributedsystems.controllers.system;

import augustopadilha.serverdistributedsystems.models.UserModel;

import java.util.HashMap;
import java.util.Map;

public class SessionController {
    private static Map<String, UserModel> sessions = new HashMap<>();

    public static void createSession(String token, UserModel userModel) {
        sessions.put(token, userModel);
    }

    public static UserModel getSessionUser(String token) {
        return sessions.get(token);
    }

    public static void removeSession(String token) {
        sessions.remove(token);
    }
}