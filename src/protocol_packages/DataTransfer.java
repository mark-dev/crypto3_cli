package protocol_packages;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 10:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataTransfer {
    private int fromId;
    private int contentType;
    private byte[] payload;

    public DataTransfer(int fromId, int contentType, byte[] payload) {
        this.fromId = fromId;
        this.contentType = contentType;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "DataTransfer{" +
                "fromId=" + fromId +
                ", contentType=" + contentType +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }

    public int getFromId() {
        return fromId;
    }

    public int getContentType() {
        return contentType;
    }

    public byte[] getPayload() {
        return payload;
    }
}
