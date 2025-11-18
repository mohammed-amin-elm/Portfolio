package commons.wsmessage;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UpdateType {
    TITLE_CHANGE,
    CONTENT_CHANGE,
    DELETED,
    ADDED,
    UNKNOWN;

    /**
     * Creator for this enum from a JSON string
     * @param key the json string
     * @return an UpdateType
     */
    @JsonCreator
    public static UpdateType fromString(String key) {
        try {
            return UpdateType.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UpdateType.UNKNOWN;
        }
    }
}
