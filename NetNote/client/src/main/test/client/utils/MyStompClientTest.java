package client.utils;

import client.DefaultCtrl;
import commons.wsmessage.Message;
import commons.wsmessage.UpdateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MyStompClientTest {

    private MyStompClient myStompClient;
    private DefaultCtrl mockDefaultCtrl;
    private StompSession mockSession;

    @BeforeEach
    void setUp() throws Exception {
        mockDefaultCtrl = mock(DefaultCtrl.class);
        mockSession = mock(StompSession.class);
        when(mockSession.getSessionId()).thenReturn("mockSessionId");
        myStompClient = Mockito.spy(new MyStompClient(mockDefaultCtrl));
        injectMockSession(myStompClient, mockSession);
    }

    private void injectMockSession(MyStompClient client, StompSession session) throws Exception {
        var sessionField = MyStompClient.class.getDeclaredField("session");
        sessionField.setAccessible(true);
        sessionField.set(client, session);
    }

    @Test
    void testGetSessionId() {
        String sessionId = myStompClient.getSessionId();
        assertEquals("mockSessionId", sessionId);
    }

    @Test
    void testSendMessage() {
        Message message = new Message(UpdateType.CONTENT_CHANGE, "Test Message", 123L);
        myStompClient.sendMessage(message);
        verify(mockSession).send("/app/message", message);
    }
}
