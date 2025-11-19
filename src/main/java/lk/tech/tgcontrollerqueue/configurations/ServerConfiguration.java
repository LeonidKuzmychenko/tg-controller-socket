package lk.tech.tgcontrollerqueue.configurations;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

//    @Bean
//    public WebClient webClient() {
//        return WebClient.builder()
//                .baseUrl("http://localhost:8282")
//                .build();
//    }
//
//    @Bean
//    public HttpRequests jsonPlaceholderClient(WebClient webClient) {
//        HttpServiceProxyFactory factory = HttpServiceProxyFactory
//                .builderFor(WebClientAdapter.create(webClient))
//                .build();
//
//        return factory.createClient(HttpRequests.class);
//    }
}
