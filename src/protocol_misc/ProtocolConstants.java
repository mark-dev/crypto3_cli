package protocol_misc;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 10:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProtocolConstants {
    public static final short PROTOCOL_LABEL = (short) 42134;
    public static final byte PT_CHANGE_KEYSPEC = 3;
    public static final byte PT_AES_KEY = 10;
    public static final byte PT_CONN_REQUEST = 2;
    public static final byte PT_AUTH_PACKET = 5;
    public static final byte PT_DATA_TRANSFER = 6;
    public static final byte PT_GET_ONLINE_USERS = 7;
    public static final byte PT_CLIENT_CONNECTED = 8;
    public static final byte PT_CLIENT_DISCONNECTED = 9;
    public static final byte PT_DATA_TRANSFER_TO_CLIENT = 13;
}

