package gui;

import entities.ChatUser;
import protocol_misc.ProtocolConstants;
import protocol_misc.ProtocolManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;

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
    private JTextField inputField;
    private File lastSelectedFile;

    public ChatDialog(ChatUser u, final ProtocolManager pm, JFrame parent) {
        setTitle(u.getLogin());
        this.user = u;
        this.pm = pm;
        setVisible(true);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        chatArea = new JTextArea("");
        chatArea.setEditable(false);
        JScrollPane sp = new JScrollPane(chatArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setPreferredSize(new Dimension(240, 300));
        add(sp, BorderLayout.CENTER);
        add(new JPanel() {{
            setLayout(new FlowLayout());
            inputField = new JTextField() {{
                setPreferredSize(new Dimension(120, 30));
                addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            sendMessage();
                            inputField.requestFocus();
                        }
                    }
                });
            }};

            add(inputField);
            add(new JButton("send") {{
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage();
                    }
                });
            }});
            add(new JButton("file") {{
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileopen = new JFileChooser();
                        int ret = fileopen.showDialog(null, "specify file");
                        if (ret == JFileChooser.APPROVE_OPTION) {
                            lastSelectedFile = fileopen.getSelectedFile();
                            inputField.setText("<file " + lastSelectedFile.getName() + ">");
                        }
                    }
                });
            }});
        }}, BorderLayout.SOUTH);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        inputField.requestFocus();
        pack();
    }

    private byte[] readBytesFromFile(File f) throws IOException {
        byte[] data = new byte[(int) f.length()];
        try {
            new FileInputStream(f).read(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private void sendMessage() {
        try {
            int contentType = ProtocolConstants.CONTENT_TYPE_TEXT;
            byte[] payload = inputField.getText().getBytes();
            if (inputField.getText().startsWith("<file")) {
                contentType = ProtocolConstants.CONTENT_TYPE_FILE;
                payload = readBytesFromFile(lastSelectedFile);
            }
            printSelfMessage(contentType, payload);
            pm.sendMessage(user.getId(), contentType, payload);
            inputField.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ChatUser getUser() {
        return user;
    }

    public void addWindowAdapter(WindowAdapter wa) {
        addWindowListener(wa);
    }

    private void appendChatArea(String msg) {
        chatArea.setText(chatArea.getText() + "\n" + msg);
    }

    private void saveIncomingFile(byte[] fileContent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("New File Received, save it");
        int retrival = chooser.showSaveDialog(this);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedOutputStream bos;
                FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile());
                bos = new BufferedOutputStream(fos);
                bos.write(fileContent);
                bos.flush();
                bos.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    public void printSelfMessage(int contentType, byte[] payload) {
        if (contentType == ProtocolConstants.CONTENT_TYPE_TEXT) {
            appendChatArea("You: " + new String(payload));
        } else if (contentType == ProtocolConstants.CONTENT_TYPE_FILE) {
            appendChatArea("You: " + "<sending file " + lastSelectedFile.getName() + " >");
        }
    }

    public void printReceivedMessage(int contentType, byte[] payload) {
        if (contentType == ProtocolConstants.CONTENT_TYPE_TEXT) {
            appendChatArea(user.getLogin() + ": " + new String(payload));
        } else if (contentType == ProtocolConstants.CONTENT_TYPE_FILE) {
            saveIncomingFile(payload);
            appendChatArea(user.getLogin() + ": " + "sended file");
        }
    }
}
