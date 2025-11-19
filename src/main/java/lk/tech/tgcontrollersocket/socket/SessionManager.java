package lk.tech.tgcontrollersocket.socket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String key, WebSocketSession session) {
        sessions.put(key, session);
    }

    public void removeSession(String key) {
        sessions.remove(key);
    }

    public WebSocketSession getSession(String key) {
        return sessions.get(key);
    }

    public Collection<WebSocketSession> getAllSessions() {
        return sessions.values();
    }
}