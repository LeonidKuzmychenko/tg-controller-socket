package lk.tech.tgcontrollersocket.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveSocketHandler implements WebSocketHandler {

    private final SessionManager sessionManager;

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        String clientKey = sessionManager.getClientKey(session);
        log.info("CLIENT CONNECTED: {}", clientKey);

        sessionManager.addSession(clientKey, session);

        return session
                .receive()                         // поток входящих сообщений
                .doOnNext(msg -> log.debug("[{}] INCOMING {}", clientKey, msg))
                .doOnError(err -> log.error("[{}] WS ERROR: {}", clientKey, err.getMessage()))
                .doFinally(signal -> {
                    sessionManager.removeSession(clientKey);
                    log.info("CLIENT DISCONNECTED [{}]: {}", signal, clientKey);
                })
                .then();
    }
}
