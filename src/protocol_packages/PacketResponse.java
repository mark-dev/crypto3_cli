package protocol_packages;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 7:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketResponse {
    public static final byte  resultCodeOK = 1;
    public static final byte  resultCodeFail = 2;
    private byte resultCode;

    public PacketResponse(byte resultCode) {
        this.resultCode = resultCode;
    }

    public byte getResultCode() {
        return resultCode;
    }

    @Override
    public String toString() {
        return "protocol_packages.PacketResponse{" +
                "resultCode=" + resultCode +
                '}';
    }
}
