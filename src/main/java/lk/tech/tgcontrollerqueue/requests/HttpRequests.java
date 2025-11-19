//package lk.tech.tgcontrollerqueue.requests;
//
//import lk.tech.tgcontrollerqueue.dto.OrderData;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.service.annotation.PostExchange;
//
//public interface HttpRequests {
//
//    @PostExchange("api/v1/answer/text/{key}")
//    void sendText(@RequestBody OrderData orderData, @PathVariable String key, @RequestParam("command") String command);
//
//    @PostExchange(value = "api/v1/answer/image/{key}", contentType = MediaType.IMAGE_PNG_VALUE)
//    void sendImage(@RequestBody byte[] bytes, @PathVariable String key, @RequestParam("command") String command);
//}