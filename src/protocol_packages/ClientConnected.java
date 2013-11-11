package protocol_packages;

import entities.ChatUser;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientConnected {
    private ChatUser user;


    public ChatUser getUser() {
        return user;
    }

    public ClientConnected(ChatUser user) {

        this.user = user;
    }
}
