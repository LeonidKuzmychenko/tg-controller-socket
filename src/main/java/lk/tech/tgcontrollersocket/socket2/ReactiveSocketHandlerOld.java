//package lk.tech.tgcontrollersocket.socket2;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lk.tech.tgcontrollersocket.dto.OrderData;
//import lk.tech.tgcontrollersocket.dto.PrefixResult;
//import lk.tech.tgcontrollersocket.requests.HttpRequests;
//import lk.tech.tgcontrollersocket.utils.BinaryUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.socket.WebSocketHandler;
//import org.springframework.web.reactive.socket.WebSocketMessage;
//import org.springframework.web.reactive.socket.WebSocketSession;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ReactiveSocketHandlerOld implements WebSocketHandler {
//
//    private final BinaryUtils binaryUtils;
//    private final HttpRequests messageSender;
//    private final ObjectMapper objectMapper;
//    private final SessionManager sessionManager;
//
//    @Override
//    public Mono<Void> handle(WebSocketSession session) {
//
//        String clientKey = sessionManager.getClientKey(session);
//
//        log.info("CLIENT CONNECTED: key={}, sessionId={}", clientKey, session.getId());
//
//        sessionManager.addSession(clientKey, session);
//
//        return session.receive()
//                .flatMap(msg -> {
//
//                    if (msg.getType() == WebSocketMessage.Type.TEXT) {
//                        String text = msg.getPayloadAsText();
//                        try {
//                            OrderData orderData = objectMapper.readValue(text, OrderData.class);
//                            log.info("TEXT from {}: {}", clientKey, text);
//                            messageSender.sendText(orderData, clientKey, orderData.command());
//                            System.out.println("Message from " + clientKey + ": " + orderData.command());
//                        } catch (JsonProcessingException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                    if (msg.getType() == WebSocketMessage.Type.BINARY) {
//                        DataBuffer payload = msg.getPayload();
//                        byte[] bytes = new byte[payload.readableByteCount()];
//                        payload.read(bytes);
//
//                        log.info("Binary message from {} ({} bytes)", clientKey, bytes.length);
//
//                        PrefixResult prefixResult = binaryUtils.extractPrefix(bytes);
//
//                        log.info("Extracted prefix '{}' '{}'", clientKey, prefixResult.prefix());
//
//                        messageSender.sendImage(prefixResult.data(), clientKey, prefixResult.prefix());
//
//                        log.info("Message from {}: {}", clientKey, prefixResult.prefix());
//                        log.info("BINARY from {}: {} bytes", clientKey, bytes.length);
//                    }
//
//                    return Mono.empty();
//                })
//                .doFinally(signal -> {
//                    // Убираем из списка при дисконнекте
//                    sessionManager.removeSession(clientKey);
//                    log.info("CLIENT DISCONNECTED: {}", clientKey);
//                })
//                .then();
//    }
//}
