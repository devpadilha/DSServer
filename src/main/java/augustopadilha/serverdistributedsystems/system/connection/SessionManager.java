package augustopadilha.serverdistributedsystems.system.connection;

import augustopadilha.serverdistributedsystems.models.Session;
import augustopadilha.serverdistributedsystems.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SessionManager {
    public static SessionManager sessionManager = null;
    private final ObservableList<Session> sessions;
    private final ObservableList<Session> loginSessions;

    public SessionManager() {
        this.sessions = FXCollections.observableArrayList();
        this.loginSessions = FXCollections.observableArrayList();
    }

    public static synchronized SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }

    public ObservableList<Session> getSessions() {
        return sessions;
    }

    public ObservableList<Session> getLoginSessions() {
        return loginSessions;
    }

    public void addSession(String ip) throws JsonProcessingException {
        sessions.add(new Session(ip));
    }

    public void updateSession(String ip,String token, User user) {
        sessions.stream().filter(session -> session.getIp().equals(ip)).forEach(session -> {
            session.setToken(token);
            session.setUser(user);
        });
        for(Session session : sessions) {
            System.out.println(session.getIp() + " " + session.getToken() + " " + session.getUser());
        }
        updateLoginSessions();
    }

    public  void removeLoginSession(String ip) throws JsonProcessingException {
        sessions.stream().filter(session -> session.getIp().equals(ip)).forEach(session -> {
            session.setToken(null);
            session.setUser(null);
        });
        updateLoginSessions();
    }

    public void removeSessions(String ip) {
        sessions.removeIf(session -> session.getIp().equals(ip));
        updateLoginSessions();
    }

    public void updateLoginSessions() {
        loginSessions.clear();
        sessions.stream().filter(session -> session.getToken() != null).forEach(loginSessions::add);
    }

    public boolean validateSession(String token) {
        return sessions.stream().anyMatch(session -> session.getToken().equals(token));
    }
}
