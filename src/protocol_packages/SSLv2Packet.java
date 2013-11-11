package protocol_packages;

import crypto_misc.AESCipher;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 5:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SSLv2Packet {
    private byte packetType;
    private byte[] payload;

    public SSLv2Packet(byte packetType, byte[] payload) {
        this.packetType = packetType;
        this.payload = payload;
    }

    public byte getPacketType() {
        return packetType;
    }

    public byte[] getPayload() {
        return payload;
    }
    public void decrypt(AESCipher aes){
        this.payload = aes.decrypt(this.payload);
    }
    @Override
    public String toString() {
        return "protocol_packages.SSLv2Packet{" +
                "packetType=" + packetType +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }
}
