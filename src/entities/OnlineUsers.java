package entities;

import entities.ChatUser;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class OnlineUsers {
    private ArrayList<ChatUser> users;

    public OnlineUsers(ArrayList<ChatUser> users) {
        this.users = users;
    }

    public ArrayList<ChatUser> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "entities.OnlineUsers{" +
                "users=" + users +
                '}';
    }
}
