package lk.tech.tgcontrollersocket.controller;

import lk.tech.tgcontrollersocket.senders.SocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class HttpController {

    private final SocketMessageSender messageSender;

    @PostMapping("/{key}")
    public void sendToClient(@PathVariable String key, @RequestParam String command) throws IOException {
        log.info("Sending to key: {}, command: {}", key, command);
        messageSender.sendToClient(key, command);
    }
}
