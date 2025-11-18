package commons.wsmessage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    private String sessionId;
    private UpdateType type;
    private String message;
    private long noteSenderId;

    /**
     * no args constructor
     */
    public Message(){}

    /**
     * constructor
     * @param type type
     * @param message message
     * @param noteSenderId note id
     */
    public Message(UpdateType type, String message, long noteSenderId) {
        this.type = type;
        this.message = message;
        this.noteSenderId = noteSenderId;
    }

    /**
     * Json creator - translates back and forth between java object and JSON
     * @param sessionId the sessionId
     * @param type type
     * @param message message
     * @param noteSenderId noteSenderId
     */
    @JsonCreator
    public Message(@JsonProperty("sessionId") String sessionId,
                   @JsonProperty("updateType") UpdateType type,
                   @JsonProperty("message") String message,
                   @JsonProperty("noteId") long noteSenderId) {
        this.sessionId = sessionId;
        this.type = type;
        this.message = message;
        this.noteSenderId = noteSenderId;
    }

    /**
     * Getter for sessionId
     * @return session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * setter for sessionId
     * @param sessionId sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * getter for type
     * @return type
     */
    public UpdateType getType() {
        return type;
    }

    /**
     * getter for message
     * @return message (string)
     */
    public String getMessage() {
        return message;
    }

    /**
     * getter for note sender id
     * @return note sender id
     */
    public long getNoteSenderId() {
        return noteSenderId;
    }

    /**
     * to string method
     * @return a human-friendly string of this object
     */
    @Override
    public String toString() {
        return type.name() + " for note " + noteSenderId + " with message: " + message;
    }
}
