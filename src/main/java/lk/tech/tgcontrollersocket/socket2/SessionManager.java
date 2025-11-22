package lk.tech.tgcontrollersocket.socket2;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

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

    public String getClientKey(WebSocketSession session) {
        String query = session.getHandshakeInfo().getUri().getQuery();
        return query != null && query.startsWith("key=")
                ? query.substring(4)
                : "unknown";
    }

}