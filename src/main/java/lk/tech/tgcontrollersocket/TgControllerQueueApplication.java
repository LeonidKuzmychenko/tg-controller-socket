package lk.tech.tgcontrollersocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@SpringBootApplication
public class TgControllerQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgControllerQueueApplication.class, args);
    }

}
