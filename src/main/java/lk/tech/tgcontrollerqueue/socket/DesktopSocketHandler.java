package lk.tech.tgcontrollerqueue.socket;

import lk.tech.tgcontrollerqueue.HttpRequests;
import lk.tech.tgcontrollerqueue.dto.OrderData;
import lk.tech.tgcontrollerqueue.senders.SocketMessageSender;
import lk.tech.tgcontrollerqueue.utils.BinaryUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import lk.tech.tgcontrollerqueue.socket.SessionManager;

import java.nio.ByteBuffer;

@Component
public class DesktopSocketHandler extends BinaryWebSocketHandler {

    private final HttpRequests httpRequests;
    private final BinaryUtils binaryUtils;
    private final SessionManager sessionManager;

    public DesktopSocketHandler(HttpRequests httpRequests, BinaryUtils binaryUtils, SessionManager sessionManager) {
        this.httpRequests = httpRequests;
        this.binaryUtils = binaryUtils;
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
//        byte[] prefix = "/screenshot:".getBytes(StandardCharsets.UTF_8);
//
//        // Проверяем префикс
//        if (hasPrefix(bytes, prefix)) {
//
//            // Вырезаем PNG как raw bytes
//            byte[] pngBytes = removePrefix(bytes, prefix.length);
//
//            System.out.println("Detected PNG image from client " + key);
//
//            // отправляем PNG в Telegram
////            messageSender.sendRawPictureToTG(key, pngBytes, "Ваш скриншот:");
//            return;
//        }

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


    public void handle(String key, String command, String data) {
        httpRequests.sendText(new OrderData(command, data), key, command);
        System.out.println("Message from " + key + ": " + command);
    }


    public void handle(String key, String command, byte[] bytes) {
//        if ("/screenshot".equals(command)){
        httpRequests.sendImage(bytes, key, command);
//        }
        System.out.println("Message from " + key + ": " + command);
    }
}
