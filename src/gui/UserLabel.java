package gui;

import entities.ChatUser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/12/13
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserLabel {
    private ChatUser user;
    private JLabel label;

    public UserLabel(ChatUser user) {
        this.user = user;
        label = new JLabel(user.getLogin());
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setPreferredSize(new Dimension(120, 30));
    }

    public JLabel getLabel() {
        return label;
    }

    public ChatUser getUser() {
        return user;
    }

    public void addListener(MouseAdapter ad) {
        label.addMouseListener(ad);
    }
}
