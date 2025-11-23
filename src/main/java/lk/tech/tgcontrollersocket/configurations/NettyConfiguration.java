package lk.tech.tgcontrollersocket.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.netty.http.server.WebsocketServerSpec;

@Configuration
public class NettyConfiguration {

    @Bean
    public WebSocketService webSocketService() {
        ReactorNettyRequestUpgradeStrategy upgrade =
                new ReactorNettyRequestUpgradeStrategy(
                        () -> WebsocketServerSpec.builder().maxFramePayloadLength(20*1024*1024));
        return new HandshakeWebSocketService(upgrade);
    }
}
