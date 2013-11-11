package entities;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatUser {
    private int id;
    private String login;

    public ChatUser(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "entities.ChatUser{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}
