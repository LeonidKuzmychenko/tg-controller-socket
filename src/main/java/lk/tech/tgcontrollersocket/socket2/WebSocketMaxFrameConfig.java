//package lk.tech.tgcontrollersocket.socket2;
//
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.channel.ChannelPipeline;
//import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
//import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class WebSocketMaxFrameConfig {
//
//    @Bean
//    public NettyServerCustomizer increaseWsFrameSize() {
//
//        return httpServer -> httpServer.doOnChannelInit((conn, channel, address) -> {
//
//            channel.pipeline().addLast("fixWsFrameSize", new ChannelInboundHandlerAdapter() {
//
//                @Override
//                public void handlerAdded(ChannelHandlerContext ctx) {
//
//                    ChannelPipeline p = ctx.pipeline();
//
//                    // Ищем ВСЕ WebSocketServerProtocolHandler
//                    for (String name : p.names()) {
//                        ChannelHandler h = p.get(name);
//
//                        if (h instanceof WebSocketServerProtocolHandler) {
//
//                            // лог — мы нашли оригинальный handler
//                            System.out.println("Replacing WebSocketServerProtocolHandler: " + name);
//
//                            p.replace(
//                                    name,
//                                    name,
//                                    new WebSocketServerProtocolHandler(
//                                            "/ws",
//                                            null,
//                                            true,
//                                            20 * 1024 * 1024 // 20 MB
//                                    )
//                            );
//
//                            break;
//                        }
//                    }
//
//                    // удаляем себя
//                    p.remove(this);
//                }
//            });
//        });
//    }
//}
