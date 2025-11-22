package lk.tech.tgcontrollersocket.socket2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.tech.tgcontrollersocket.dto.OrderData;
import lk.tech.tgcontrollersocket.dto.PrefixResult;
import lk.tech.tgcontrollersocket.requests.HttpRequests;
import lk.tech.tgcontrollersocket.utils.BinaryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveSocketHandler implements WebSocketHandler {

    private final BinaryUtils binaryUtils;
    private final HttpRequests messageSender;
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        String clientKey = sessionManager.getClientKey(session);
        sessionManager.addSession(clientKey, session);

        return session.receive()
                .flatMap(msg -> {
                    if (msg.getType() == WebSocketMessage.Type.TEXT) {
                        return handleText(msg, clientKey);
                    }
                    if (msg.getType() == WebSocketMessage.Type.BINARY) {
                        return handleBinaryMessage(msg, clientKey);
                    }
                    return Mono.empty();
                })
                .doOnError(e -> log.error("WS ERROR", e))
                .doFinally(sig -> {
                    sessionManager.removeSession(clientKey);
                    log.info("CLIENT DISCONNECTED: {}", clientKey);
                })
                // 🔥 Не закрывать WebSocket после первого сообщения!
                .then(Mono.never());
    }

    private Mono<Void> handleBinaryMessage(WebSocketMessage msg, String clientKey) {

        DataBuffer buffer = msg.getPayload(); // уже aggregated в WebFlux

        byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        DataBufferUtils.release(buffer);

        return handleBinary(bytes, clientKey);
    }

    private Mono<Void> handleText(WebSocketMessage msg, String clientKey) {
        try {
            String text = msg.getPayloadAsText();
            OrderData orderData = objectMapper.readValue(text, OrderData.class);

            log.info("TEXT from {}: {}", clientKey, text);
            messageSender.sendText(orderData, clientKey, orderData.command());

        } catch (Exception e) {
            log.error("TEXT parse error", e);
        }

        return Mono.empty();
    }

    private Mono<Void> handleBinary(byte[] bytes, String clientKey) {

        log.info("Binary message from {} ({} bytes)", clientKey, bytes.length);

        PrefixResult prefixResult = binaryUtils.extractPrefix(bytes);

        log.info("Extracted prefix '{}' from {}", prefixResult.prefix(), clientKey);

        messageSender.sendImage(prefixResult.data(), clientKey, prefixResult.prefix());

        return Mono.empty();
    }

}
