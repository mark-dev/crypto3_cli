package gui;

import entities.ChatUser;
import protocol_misc.ProtocolEventsAdapter;
import protocol_misc.ProtocolManager;
import protocol_packages.DataTransfer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/12/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsersFrame extends JFrame {
    private ArrayList<UserLabel> userLabels = new ArrayList<UserLabel>();
    private ArrayList<ChatDialog> chatDialogs = new ArrayList<ChatDialog>();
    private ProtocolManager pm;
    private JPanel userPanel;
    private JPanel controlPanel;

    public UsersFrame(String login, String password, byte[] cert) throws Exception {
        setTitle("Login as " + login);
        userPanel = new JPanel();
        controlPanel = new JPanel();
        pm = new ProtocolManager(cert);
        pm.startHandShake(login, password);
        ArrayList<ChatUser> online = pm.getOnlineUsers().getUsers();
        pm.receiveMessages();

        setLayout(new BorderLayout());
        controlPanel.setLayout(new FlowLayout());
        userPanel.setLayout(new GridLayout(10, 1));
        for (final ChatUser user : online) {
            UserLabel ul = newUserLabel(user);
            userLabels.add(ul);
            userPanel.add(ul.getLabel());
        }
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(300, 300));
        controlPanel.add(new JButton("reInitKey") {{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pm.reInitAESKey();
                }
            });
        }});
        pm.addProtocolEventListneer(new ProtocolEventsAdapter() {
            @Override
            public void onClientConnected(ChatUser c) {
                onUserConnected(c);
            }

            @Override
            public void onClientDisconnected(int userId) {
                onUserDisconnected(userId);
            }

            @Override
            public void onDataReceived(DataTransfer dt) {
                processIncomingData(dt);
            }
        });
        add(userPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);


        pack();
    }

    private void processIncomingData(DataTransfer dt) {
        for (ChatDialog dialog : chatDialogs) {
            if (dialog.getUser().getId() == dt.getFromId()) {
                //уже есть открытый диалог.
                dialog.requestFocus();
                dialog.printReceivedMessage(dt.getContentType(), dt.getPayload());
                return;
            }
        }
        //Ищем пользователя с таким id и открываем с ним диалог
        for (UserLabel label : userLabels) {
            if (label.getUser().getId() == dt.getFromId()) {
                createNewDialog(label.getUser());
                chatDialogs.get(chatDialogs.size() - 1).printReceivedMessage(dt.getContentType(), dt.getPayload());
            }
        }
    }

    private void openChatDialog(UserLabel ul) {
        for (ChatDialog dialog : chatDialogs) {
            if (dialog.getUser().equals(ul.getUser())) {
                //уже есть открытый диалог.
                dialog.requestFocus();
                return;
            }
        }
        createNewDialog(ul.getUser());
    }

    private void createNewDialog(ChatUser user) {
        //Небыло диалогов с этим пользователем
        final ChatDialog newDialog = new ChatDialog(user, pm, this);
        chatDialogs.add(newDialog);
        newDialog.addWindowAdapter(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                chatDialogs.remove(newDialog);
            }
        });
    }

    private void onUserConnected(ChatUser user) {
        UserLabel ul = newUserLabel(user);
        userLabels.add(ul);
        userPanel.add(ul.getLabel());
        pack();
    }

    private UserLabel newUserLabel(final ChatUser user) {
        final UserLabel ul = new UserLabel(user);
        ul.addListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openChatDialog(ul);
            }
        });
        return ul;
    }

    private void onUserDisconnected(int userId) {
        for (UserLabel label : userLabels) {
            if (label.getUser().getId() == userId) {
                remove(label.getLabel());
                userLabels.remove(label);
                pack();
                break;
            }
        }
    }


    public static void main(String[] args) throws Exception {
        String login = args[0];
        String password = args[1];
        byte[] cert = {1, 2, 3};
        new UsersFrame(login, password, cert).setVisible(true);
    }
}
