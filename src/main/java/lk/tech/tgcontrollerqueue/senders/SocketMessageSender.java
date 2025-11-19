//package lk.tech.tgcontrollerqueue.senders;
//
//import lk.tech.tgcontrollerqueue.socket.SessionManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.io.IOException;
//
//@Service
//@RequiredArgsConstructor
//public class SocketMessageSender {
//
//    private final SessionManager sessionManager;
//
//    public void sendToClient(String key, String command) throws IOException {
//        WebSocketSession session = sessionManager.getSession(key);
//
//        if (session != null && session.isOpen()) {
//            session.sendMessage(new TextMessage(command));
//        } else {
//            System.out.println("Client not connected: " + key);
//        }
//    }
//}
