package lk.tech.tgcontrollersocket.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketMessageSender {

    private final SessionManager sessionManager;

    public Mono<Void> sendToClient(String clientKey, String text) {

        WebSocketSession session = sessionManager.getSession(clientKey);

        if (session == null) {
            log.warn("SEND FAILED: client {} not connected", clientKey);
            return Mono.empty();
        }

        if (!session.isOpen()) {
            log.warn("SEND FAILED: session for client {} is closed", clientKey);
            return Mono.empty();
        }

        return session.send(
                        Mono.just(session.textMessage(text))
                )
                .doOnError(err ->
                        log.error("WS SEND ERROR [{}]: {}", clientKey, err.getMessage())
                )
                .doOnSuccess(v ->
                        log.debug("WS SEND OK [{}]: {}", clientKey, text)
                );
    }
}
