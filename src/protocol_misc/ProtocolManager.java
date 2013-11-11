package protocol_misc;

import crypto_misc.AESCipher;
import crypto_misc.RSADecode;
import crypto_misc.RSAEncode;
import entities.OnlineUsers;
import protocol_packages.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProtocolManager {
    enum HandshakeAcitons {
        ENCRYPT_TEST,
        CERTIFICATE,
        AUTH
    }

    private ArrayList<ProtocolEventsAdapter> listeners = new ArrayList<ProtocolEventsAdapter>();
    private Socket socket;
    private RSADecode rsaDecode;
    private RSAEncode rsaEncode;
    private AESCipher aesCipher;
    private byte[] clientCert;

    public ProtocolManager(byte[] clientCert) throws Exception {
        socket = new Socket("localhost", 2222);
        this.clientCert = clientCert;
    }


    public void addProtocolEventListneer(ProtocolEventsAdapter adapter) {
        listeners.add(adapter);
    }

    public void removeProtocolEventListneer(ProtocolEventsAdapter adapter) {
        listeners.remove(adapter);
    }

    public void startHandShake(String login, String password) throws Exception {
        startRSAKeyExchange();
        readServerRSAPubKey();
        sendAESKey();
        ensureServerRetunsOk(HandshakeAcitons.ENCRYPT_TEST);
        sendCertificate();
        ensureServerRetunsOk(HandshakeAcitons.CERTIFICATE);
        doAuth(login, password);
        ensureServerRetunsOk(HandshakeAcitons.AUTH);
    }

    public OnlineUsers getOnlineUsers() {
        try {
            byte[] getOnlineUsersRequest = SSLv2Protocol.encodeGetOnlineUsers(aesCipher);
            sendViaSocket(getOnlineUsersRequest);
            SSLv2Packet reply = readPacket();
            reply.decrypt(aesCipher);
            return SSLv2PacketParser.decodeAsOnlineUsersResponse(reply);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(int toUser, int contentType, byte[] payload) throws IOException {
        byte[] transfer = SSLv2Protocol.encodeDataTransfer((byte) toUser, (byte) contentType, payload, aesCipher);
        sendViaSocket(transfer);
    }

    public void receiveMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        SSLv2Packet packet = readPacket();
                        packet.decrypt(aesCipher);
                        switch (packet.getPacketType()) {
                            case ProtocolConstants.PT_CLIENT_CONNECTED: {
                                ClientConnected cc = SSLv2PacketParser.decodeAsClientConnected(packet);
                                for (ProtocolEventsAdapter ad : listeners) {
                                    ad.onClientConnected(cc.getUser());
                                }
                                break;
                            }
                            case ProtocolConstants.PT_DATA_TRANSFER_TO_CLIENT: {
                                DataTransfer dt = SSLv2PacketParser.decodeAsDataTransfer(packet);
                                for (ProtocolEventsAdapter ad : listeners) {
                                    ad.onDataReceived(dt);
                                }
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void sendViaSocket(byte[] data) {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SSLv2Packet readPacket() {
        try {
            DataInputStream is = new DataInputStream(socket.getInputStream());
            short protocolLabel = is.readShort();
            byte packetType = is.readByte();
            short contentLen = is.readShort();
            byte[] buf = new byte[contentLen];
            int readed = 0;
            while (readed < contentLen) {
                buf[readed] = (byte) is.read();
                readed++;
            }
            SSLv2Packet packet = new SSLv2Packet(packetType, buf);
            System.out.println("RX: " + packet);
            return packet;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startRSAKeyExchange() {
        int keyBitLen = 1024;
        BigInteger one = new BigInteger("1");
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(keyBitLen / 2, random);
        BigInteger q = BigInteger.probablePrime(keyBitLen / 2, random);
        BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));

        BigInteger modulus = p.multiply(q);
        BigInteger publicKey = new BigInteger("65537");
        BigInteger privateKey = publicKey.modInverse(phi);

        try {
            rsaDecode = new RSADecode(modulus, privateKey);
            byte[] changeCipher = SSLv2Protocol.encodeChangeKeySpec(modulus, publicKey);
            sendViaSocket(changeCipher);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readServerRSAPubKey() {
        SSLv2Packet packet = readPacket();
        try {
            ChangeCipherSpec spec = SSLv2PacketParser.decodeAsChangeCipher(packet);
            rsaEncode = new RSAEncode(spec.getModulus(), spec.getPubExp());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendAESKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            SecretKey secretKey = kg.generateKey();
            aesCipher = new AESCipher(secretKey);
            byte[] aeskey = secretKey.getEncoded();
            byte[] aesPacket = SSLv2Protocol.encodeAESkey(aeskey, rsaEncode);
            sendViaSocket(aesPacket);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ensureServerRetunsOk(HandshakeAcitons act) throws Exception {
        SSLv2Packet packet = readPacket();
        packet.decrypt(aesCipher);
        PacketResponse packetResponse = SSLv2PacketParser.decodeAsPacketResponse(packet);
        if (packetResponse.getResultCode() != PacketResponse.resultCodeOK) {
            System.out.println("expected OK but found " + packetResponse.getResultCode());
            throw new Exception("result code for action " + act + " =/= OK");
        }
    }

    private void sendCertificate() {
        try {
            byte[] certPacket = SSLv2Protocol.encodeCertificate(clientCert, aesCipher);
            sendViaSocket(certPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void doAuth(String login, String password) {
        try {
            byte[] authPacket = SSLv2Protocol.encodeAuthPacket(login, password, aesCipher);
            sendViaSocket(authPacket);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
