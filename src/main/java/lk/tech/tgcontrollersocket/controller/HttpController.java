package lk.tech.tgcontrollersocket.controller;

import lk.tech.tgcontrollersocket.socket.SocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class HttpController {

    private final SocketMessageSender messageSender;

    @PostMapping("/{key}")
    public Mono<Void> sendToClient(@PathVariable String key, @RequestParam String command) throws IOException {
        log.info("Sending to key: {}, command: {}", key, command);
        return messageSender.sendToClient(key, command);
    }
}
