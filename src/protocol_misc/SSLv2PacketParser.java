package protocol_misc;

import entities.ChatUser;
import entities.OnlineUsers;
import protocol_packages.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SSLv2PacketParser {
    public static ChangeCipherSpec decodeAsChangeCipher(SSLv2Packet packet) throws IOException {
        byte[] payload = packet.getPayload();
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream is = new DataInputStream(bais);
        int modLen = is.readUnsignedShort();
        byte[] modulus = new byte[modLen];
        int pubLen = payload.length - modLen - 2;
        byte[] pubExp = new byte[pubLen];
        is.read(modulus, 0, modLen);
        is.read(pubExp, 0, pubLen);
        return new ChangeCipherSpec(new BigInteger(new String(modulus)), new BigInteger(new String(pubExp)));
    }

    public static PacketResponse decodeAsPacketResponse(SSLv2Packet packet) {
        return new PacketResponse(packet.getPayload()[0]);
    }

    public static OnlineUsers decodeAsOnlineUsersResponse(SSLv2Packet packet) throws IOException {
        byte[] payload = packet.getPayload();
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream is = new DataInputStream(bais);
        int total = is.readUnsignedByte();
        int readed = 0;
        ArrayList<ChatUser> users = new ArrayList<ChatUser>();
        while (readed < total) {
            int userId = is.readUnsignedByte();
            int loginLen = is.readUnsignedByte();
            byte[] login = new byte[loginLen];
            is.read(login, 0, loginLen);
            users.add(new ChatUser(userId, new String(login)));
            readed++;
        }
        return new OnlineUsers(users);
    }

    public static ClientConnected decodeAsClientConnected(SSLv2Packet packet) throws IOException {
        byte[] payload = packet.getPayload();
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream is = new DataInputStream(bais);
        int userId = is.readUnsignedByte();
        int loginLen = is.readUnsignedByte();
        byte[] login = new byte[loginLen];
        is.read(login, 0, loginLen);
        return new ClientConnected(new ChatUser(userId, new String(login)));
    }

    public static DataTransfer decodeAsDataTransfer(SSLv2Packet packet) throws IOException {
        byte[] payload = packet.getPayload();
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream is = new DataInputStream(bais);
        int from = is.readUnsignedByte();
        int contentType = is.readUnsignedByte();
        byte[] content = new byte[payload.length - 2];
        is.read(content, 0, content.length);
        return new DataTransfer(from, contentType, content);
    }
}
