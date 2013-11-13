package protocol_misc;

import crypto_misc.AESCipher;
import crypto_misc.IEncoder;
import crypto_misc.RSADecode;
import crypto_misc.RSAEncode;
import entities.ChatUser;
import protocol_packages.DataTransfer;
import protocol_packages.SSLv2Packet;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 5:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SSLv2Protocol {


    public static byte[] encodePacketType(byte PacketType, byte[] content) throws IOException {
        int contentLen = content != null ? content.length : 0;
        ByteBuffer buffer = ByteBuffer.allocate(2 + contentLen + 1 + 2);
        buffer.putShort(ProtocolConstants.PROTOCOL_LABEL);
        buffer.put(PacketType);
        if (content != null) {
            buffer.putShort((short) content.length);
            buffer.put(content);
        } else {
            buffer.putShort((short) 0);
        }
        return buffer.array();
    }

    public static byte[] encodeChangeKeySpec(BigInteger modulus, BigInteger publicExp) throws IOException {
        byte[] mod = modulus.toString().getBytes();
        byte[] pubExp = publicExp.toString().getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(mod.length + pubExp.length + 2);
        buffer.putShort((short) mod.length);
        buffer.put(mod);
        buffer.put(pubExp);
        return encodePacketType(ProtocolConstants.PT_CHANGE_KEYSPEC, buffer.array());
    }

    public static byte[] encodeAESkey(byte[] key, IEncoder enc) throws IOException {
        return enc.enc(encodePacketType(ProtocolConstants.PT_AES_KEY, key));
    }

    public static byte[] encodeCertificate(byte[] certificate, AESCipher cipher) throws IOException {
        return cipher.encrypt(encodePacketType(ProtocolConstants.PT_CONN_REQUEST, certificate));
    }

    public static byte[] encodeAuthPacket(String login, String password, AESCipher cipher) throws IOException, NoSuchAlgorithmException {
        byte[] md5Password = MessageDigest.getInstance("MD5").digest(password.getBytes());
        byte[] loginBytes = login.getBytes();
        ByteBuffer bb = ByteBuffer.allocate(1 + md5Password.length + loginBytes.length);
        bb.put((byte) loginBytes.length);
        bb.put(loginBytes);
        bb.put(md5Password);
        return cipher.encrypt(encodePacketType(ProtocolConstants.PT_AUTH_PACKET, bb.array()));
    }

    public static byte[] encodeGetOnlineUsers(AESCipher cipher) throws IOException {
        return cipher.encrypt(encodePacketType(ProtocolConstants.PT_GET_ONLINE_USERS, null));
    }

    public static byte[] encodeDataTransfer(byte recipient, byte contentType, byte[] content, AESCipher cipher) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(1 + 1 + content.length);
        bb.put(recipient);
        bb.put(contentType);
        bb.put(content);
        return cipher.encrypt((encodePacketType(ProtocolConstants.PT_DATA_TRANSFER, bb.array())));
    }
}
