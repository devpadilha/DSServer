package augustopadilha.serverdistributedsystems.controllers.system;

import augustopadilha.serverdistributedsystems.models.User;

import java.util.HashMap;
import java.util.Map;

public class SessionController {
    private static Map<String, User> sessions = new HashMap<>();

    public static void createSession(String token, User user) {
        sessions.put(token, user);
    }

    public static User getSessionUser(String token) {
        return sessions.get(token);
    }

    public static void removeSession(String token) {
        sessions.remove(token);
    }
}