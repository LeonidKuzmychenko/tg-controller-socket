//package lk.tech.tgcontrollersocket.socket;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lk.tech.tgcontrollersocket.dto.OrderData;
//import lk.tech.tgcontrollersocket.requests.HttpRequests;
//import lk.tech.tgcontrollersocket.utils.BinaryUtils;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SocketController extends AbstractSocketHandler {
//
//    private final HttpRequests messageSender;
//
//    public SocketController(ObjectMapper objectMapper, SessionManager sessionManager, BinaryUtils binaryUtils, HttpRequests messageSender) {
//        super(objectMapper, sessionManager, binaryUtils);
//        this.messageSender = messageSender;
//    }
//
//    @Override
//    public void handle(String key, String command, String data) {
//        messageSender.sendText(new OrderData(command, data), key, command);
//        System.out.println("Message from " + key + ": " + command);
//    }
//
//    @Override
//    public void handle(String key, String command, byte[] bytes) {
////        if ("/screenshot".equals(command)){
//            messageSender.sendImage(bytes, key, command);
////        }
//        System.out.println("Message from " + key + ": " + command);
//    }
//}
