package lk.tech.tgcontrollerqueue.controller;

import lk.tech.tgcontrollerqueue.senders.SocketMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class HttpController {

    private final SocketMessageSender messageSender;

    @PostMapping("/{key}")
    public void sendToClient(@PathVariable String key, @RequestParam String command) throws IOException {
        messageSender.sendToClient(key, command);
    }
}
