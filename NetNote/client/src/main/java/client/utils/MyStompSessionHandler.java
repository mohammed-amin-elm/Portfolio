package client.utils;

import client.DefaultCtrl;
import org.springframework.messaging.simp.stomp.*;
import commons.wsmessage.Message;

import java.lang.reflect.Type;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private final DefaultCtrl defaultCtrl;

    /**
     * Constructor
     * @param defaultCtrl defaultCtrl
     */
    public MyStompSessionHandler(DefaultCtrl defaultCtrl) {
        this.defaultCtrl = defaultCtrl;
    }

    /**
     * Subscribes to the channel /topic/messages to listen
     * for incoming messages
     * @param session session
     * @param connectedHeaders headers
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("new client connected");
        session.subscribe("/topic/messages", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    if(payload instanceof Message message) {
                        defaultCtrl.handleWebSocketMessage(message);
                    } else {
                        System.out.println("received unexpected type: " + payload.getClass());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
