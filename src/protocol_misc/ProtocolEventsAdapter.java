package protocol_misc;

import entities.ChatUser;
import protocol_packages.DataTransfer;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ProtocolEventsAdapter {
    public abstract void onClientConnected(ChatUser c);
    public abstract void onClientDisconnected(int userId);
    public abstract void onDataReceived(DataTransfer dt);
}
