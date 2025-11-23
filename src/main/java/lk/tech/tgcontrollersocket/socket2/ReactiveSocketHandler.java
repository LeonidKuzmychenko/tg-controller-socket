package lk.tech.tgcontrollersocket.socket2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.tech.tgcontrollersocket.dto.OrderData;
import lk.tech.tgcontrollersocket.dto.PrefixResult;
import lk.tech.tgcontrollersocket.requests.HttpRequests;
import lk.tech.tgcontrollersocket.utils.BinaryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveSocketHandler implements WebSocketHandler {

    private final BinaryUtils binaryUtils;
    private final HttpRequests messageSender; // блокирующий HttpService
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
                .then(Mono.never()); // не закрываем WebSocket
    }

    private Mono<Void> handleBinaryMessage(WebSocketMessage msg, String clientKey) {

        // копируем данные пока refCnt > 0
        byte[] bytes = new byte[msg.getPayload().readableByteCount()];
        msg.getPayload().read(bytes);

        // дальше можно выполнять в блокирующем таске
        return Mono.fromRunnable(() -> handleBinaryBlocking(bytes, clientKey))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private Mono<Void> handleText(WebSocketMessage msg, String clientKey) {
        String text = msg.getPayloadAsText(); // <-- читаем СРАЗУ, пока buffer ещё живой

        // дальше — уже НЕ используем msg
        return Mono.fromRunnable(() -> {
            try {
                OrderData orderData = objectMapper.readValue(text, OrderData.class);

                log.info("TEXT from {}: {}", clientKey, text);

                messageSender.sendText(orderData, clientKey, orderData.command());

            } catch (Exception e) {
                log.error("TEXT parse error", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    /**
     * Блокирующий метод — только в boundedElastic!
     */
    private void handleBinaryBlocking(byte[] bytes, String clientKey) {

        log.info("Binary message from {} ({} bytes)", clientKey, bytes.length);

        PrefixResult prefixResult = binaryUtils.extractPrefix(bytes);

        log.info("Extracted prefix '{}' from {}", prefixResult.prefix(), clientKey);

        // B L O C K I N G
        messageSender.sendImage(prefixResult.data(), clientKey, prefixResult.prefix());
    }
}
