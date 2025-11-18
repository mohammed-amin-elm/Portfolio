package server.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import commons.wsmessage.Message;

import java.nio.charset.StandardCharsets;

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructor
     * @param messagingTemplate injected messaging template
     */
    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * handler for the messages
     * @param payload
     * @throws JsonProcessingException
     */
    @MessageMapping("/message")
    public void handleMessage(@Payload byte[] payload) throws JsonProcessingException {
        String message = new String(payload, StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        Message deserializedMessage = objectMapper.readValue(message, Message.class);
        System.out.println("Deserialized Message: " + deserializedMessage);

        messagingTemplate.convertAndSend("/topic/messages", deserializedMessage);
    }
}
