package lk.tech.tgcontrollersocket.socket2;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.adapter.NettyWebSocketSessionSupport;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.WebsocketClientSpec;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.WebsocketServerSpec;

import java.util.Map;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class ReactiveWebSocketConfig {

    private final ReactiveSocketHandler handler;

    private static final int MAX_FRAME_SIZE_MB = 25;
    private static final int MAX_FRAME_SIZE = MAX_FRAME_SIZE_MB * 1024 * 1024 * 10; // 1 МБ

//    /**
//     * @Primary гарантирует, что Spring использует ИМЕННО эту стратегию, а не дефолтную.
//     * Мы используем стандартный конструктор с WebsocketServerSpec.
//     */
//    @Bean
//    @Primary
//    public WebSocketService webSocketService() {
//
//        ReactorNettyRequestUpgradeStrategy strategy = getReactorNettyRequestUpgradeStrategy();
//
//        // ВАЖНО: На некоторых версиях Spring Framework (например, 6.0.15) этот
//        // конструктор может быть проигнорирован из-за бага, и лимит 64КБ сохранится.
//        return new HandshakeWebSocketService(strategy);
//    }

    @Bean
    @Primary
    public ReactorNettyRequestUpgradeStrategy getReactorNettyRequestUpgradeStrategy() {
        Supplier<WebsocketServerSpec.Builder> specSupplier =
                () -> WebsocketServerSpec.builder().maxFramePayloadLength(MAX_FRAME_SIZE);

        return new ReactorNettyRequestUpgradeStrategy(specSupplier);
    }

    @Bean
    public HandlerMapping webSocketMapping() {
        Map<String, WebSocketHandler> map = Map.of(
                "/ws", handler
        );

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(1);
        mapping.setUrlMap(map);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
//        NettyWebSocketSessionSupport.DEFAULT_FRAME_MAX_SIZE
        return new WebSocketHandlerAdapter();
    }

//    @Bean
//    public WebSocketService webSocketService() {
//        ReactorNettyRequestUpgradeStrategy strategy = new ReactorNettyRequestUpgradeStrategy();
//        strategy.setMaxFramePayloadLength(MAX_FRAME_SIZE);
//        return new HandshakeWebSocketService(strategy);
//    }

}
