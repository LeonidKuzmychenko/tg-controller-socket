package lk.tech.tgcontrollersocket.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.tech.tgcontrollersocket.dto.OrderData;
import lk.tech.tgcontrollersocket.dto.PrefixResult;
import lk.tech.tgcontrollersocket.utils.BinaryUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public abstract class AbstractSocketHandler extends BinaryWebSocketHandler {

    public final ObjectMapper objectMapper;
    public final SessionManager sessionManager;
    public final BinaryUtils binaryUtils;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Читаем ключ из query params
        String key = getClientKey(session);

        if (key == null || key.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Missing key"));
            return;
        }

        session.getAttributes().put("clientKey", key);
        sessionManager.addSession(key, session);


        System.out.println("Connected: " + key);
    }

    @SneakyThrows
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String key = (String) session.getAttributes().get("clientKey");
        String data = message.getPayload();
        OrderData orderData = objectMapper.readValue(data, OrderData.class);
        System.out.println("Message from " + key + ": " + orderData.command());
        handle(key, orderData.command(), orderData.data());
    }

    public abstract void handle(String key, String command, String data);

    @SneakyThrows
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String key = (String) session.getAttributes().get("clientKey");
        ByteBuffer buffer = message.getPayload();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        System.out.println("Binary message from " + key + " (" + bytes.length + " bytes)");

        PrefixResult prefixResult = binaryUtils.extractPrefix(bytes);

        System.out.println("Extracted prefix = " + key + prefixResult.prefix());
        handle(key, prefixResult.prefix(), prefixResult.data());
    }

    public abstract void handle(String key, String command, byte[] data);


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String key = (String) session.getAttributes().get("clientKey");
        sessionManager.removeSession(key);

        System.out.println("Disconnected: " + key);
    }

    private String getClientKey(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null) return null;

        for (String p : query.split("&")) {
            String[] kv = p.split("=");
            if (kv.length == 2 && kv[0].equals("key")) {
                return kv[1];
            }
        }
        return null;
    }
}
