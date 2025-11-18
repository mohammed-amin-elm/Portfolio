package client.utils;
import client.DefaultCtrl;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import commons.wsmessage.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyStompClient {
    private StompSession session;

    /**
     * Constructor
     * @param defaultCtrl defaultCtrl
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    public MyStompClient(DefaultCtrl defaultCtrl) throws ExecutionException, InterruptedException {
        String url = "ws://" + "localhost:8080" + "/ws";

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(sockJsClient);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler stompSessionHandler = new MyStompSessionHandler(defaultCtrl);

        session = webSocketStompClient.connectAsync(url, stompSessionHandler).get();
    }

    /**
     * getter for session id
     * @return session id
     */
    public String getSessionId() {
        return session.getSessionId();
    }

    /**
     * sends a message to the server
     * @param message the message
     */
    public void sendMessage(Message message) {
        try {
            message.setSessionId(session.getSessionId());
            session.send("/app/message", message);
            System.out.println("Message Sent : " + message.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
