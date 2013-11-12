package gui;

import entities.ChatUser;
import protocol_misc.ProtocolManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/12/13
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatDialog extends JFrame {
    final private ChatUser user;
    final private ProtocolManager pm;
    final private JTextArea chatArea;

    public ChatDialog(ChatUser u, final ProtocolManager pm,JFrame parent) {
        setTitle(u.getLogin());
        this.user = u;
        this.pm = pm;
        setVisible(true);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        chatArea = new JTextArea("");
        JScrollPane sp = new JScrollPane(chatArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setPreferredSize(new Dimension(240, 300));
        add(sp, BorderLayout.CENTER);
        add(new JPanel() {{
            setLayout(new FlowLayout());
            final JTextField inputField = new JTextField() {{
                setPreferredSize(new Dimension(120, 30));
            }};
            add(inputField);
            add(new JButton("send") {{
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            printSelfMessage(1, inputField.getText().getBytes());
                            pm.sendMessage(user.getId(), 1, inputField.getText().getBytes());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }});
        }}, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }

    public ChatUser getUser() {
        return user;
    }

    public void addWindowAdapter(WindowAdapter wa) {
        addWindowListener(wa);
    }

    public void printSelfMessage(int contentType, byte[] payload) {
        chatArea.setText(chatArea.getText() + "\n" + "You" + ": " + new String(payload));
    }

    public void notifyNewMessage(int contentType, byte[] payload) {
        chatArea.setText(chatArea.getText() + "\n" + user.getLogin() + ": " + new String(payload));
    }
}
