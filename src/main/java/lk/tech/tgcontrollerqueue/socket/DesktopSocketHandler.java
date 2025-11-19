package lk.tech.tgcontrollerqueue.socket;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import lk.tech.tgcontrollerqueue.socket.SessionManager;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class DesktopSocketHandler extends BinaryWebSocketHandler {

    private final SessionManager sessionManager;

    public DesktopSocketHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Читаем ключ из query params
        String key = getClientKey(session);

        if (key == null || key.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Missing key"));
            return;
        }

        session.getAttributes().put("clientKey", key);
//        clients.put(key, session);

        sessionManager.addSession(key, session);

        System.out.println("Connected: " + key);
    }

    @SneakyThrows
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String key = (String) session.getAttributes().get("clientKey");
        String data = message.getPayload();
//        Answer answer = new ObjectMapper().readValue(data, Answer.class);
//        String command = answer.getCommand();
//        System.out.println("Message from " + key + ": " + command);
    }

    // ───────────────────────────────────────────────────────────────
    // BINARY MESSAGE (PNG)
    // ───────────────────────────────────────────────────────────────
    @SneakyThrows
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String key = (String) session.getAttributes().get("clientKey");
        ByteBuffer buffer = message.getPayload();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        System.out.println("Binary message from " + key + " (" + bytes.length + " bytes)");

        // Правильный префикс
        byte[] prefix = "/screenshot:".getBytes(StandardCharsets.UTF_8);

        // Проверяем префикс
        if (hasPrefix(bytes, prefix)) {

            // Вырезаем PNG как raw bytes
            byte[] pngBytes = removePrefix(bytes, prefix.length);

            System.out.println("Detected PNG image from client " + key);

            // отправляем PNG в Telegram
//            messageSender.sendRawPictureToTG(key, pngBytes, "Ваш скриншот:");
            return;
        }

        System.out.println("Unknown binary message type from " + key);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String key = (String) session.getAttributes().get("clientKey");
//        clients.remove(key);
        sessionManager.removeSession(key);

        System.out.println("Disconnected: " + key);
    }

    private String getClientKey(WebSocketSession session) {
        String query = session.getUri().getQuery(); // key=DESKTOP123
        if (query == null) return null;

        for (String p : query.split("&")) {
            String[] kv = p.split("=");
            if (kv.length == 2 && kv[0].equals("key")) {
                return kv[1];
            }
        }
        return null;
    }

    private boolean hasPrefix(byte[] src, byte[] prefix) {
        if (src.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (src[i] != prefix[i]) return false;
        }
        return true;
    }

    private byte[] removePrefix(byte[] src, int prefixSize) {
        byte[] out = new byte[src.length - prefixSize];
        System.arraycopy(src, prefixSize, out, 0, out.length);
        return out;
    }
}
