package lk.tech.tgcontrollersocket.socket2;

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

    public boolean sendToClient(String clientKey, String text) {
        WebSocketSession session = sessionManager.getSession(clientKey);

        if (session == null || !session.isOpen()) {
            log.warn("SEND FAILED: client {} not connected", clientKey);
            return false;
        }

        session.send(Mono.just(session.textMessage(text)))
                .subscribe(
                        null,
                        err -> log.error("Error while sending to {}: {}", clientKey, err.getMessage())
                );

        return true;
    }
}
