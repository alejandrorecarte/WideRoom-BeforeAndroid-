package controllers.frameControllers;

import models.Chat;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MainFrame {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    public static JFrame mainFrame;
    private JLabel chatsLabel;
    private JPanel mainPanel;
    private JLabel wideRoomLabel;
    private JScrollPane chatsScrollPane;
    private JPanel chatsPanel;
    private JPanel chatPanel;
    private JLabel usernameLabel;
    private static User user;
    private static ArrayList<Chat> chats;
    private static Chat chatEscogido;

    public static void startUI(User user) {
        mainFrame = new JFrame("WideRoom");
        mainFrame.setContentPane(new MainFrame(user).mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setIconImage(new ImageIcon("src/main/java/icons/LogoPlanoNoTitle.png").getImage());
        mainFrame.setBounds(0,0,WIDTH,HEIGHT);
    }

    public MainFrame(User user) {
        this.chats = new ArrayList<Chat>();
        this.user = user;
        usernameLabel.setText("Bienvenid@ " + user.getUsername());
        Image icon = new ImageIcon("src/main/java/icons/LogoBlanco.png").getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
        wideRoomLabel.setIcon(new ImageIcon(icon));

        actualizarChatList();

        File filesDir = new File("src/files");
        if(!filesDir.exists()){
            filesDir.mkdir();
        }
        File clientDir = new File("src/files/client");
        if(!clientDir.exists()){
            clientDir.mkdir();
        }
        File serverDir = new File("src/files/server");
        if(!serverDir.exists()){
            serverDir.mkdir();
        }

    }

    public void actualizarChatList() {
        try {
            chatsPanel.removeAll();
            chatsPanel.setLayout(new BoxLayout(chatsPanel, BoxLayout.Y_AXIS));
            for (int i = 0; i < chats.size(); i++) {
                final int finalI = i;
                JButton b = new JButton(chats.get(i).getUsers().getLast().getUsername());
                chatsPanel.add(b);
                b.setBackground(Color.decode("#272727"));
                b.setForeground(Color.WHITE);
                b.setFont(new Font("arial", Font.PLAIN, 16));
                b.setFocusPainted(false);
                b.setMaximumSize(new Dimension(Integer.MAX_VALUE, b.getPreferredSize().height));

                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for(int i = 0; i < chatsPanel.getComponentCount(); i++){
                            chatsPanel.getComponent(i).setBackground(Color.decode("#272727"));
                            chatsPanel.getComponent(i).setForeground(Color.WHITE);
                        }
                        b.setBackground(Color.decode("#f14c8e"));
                        b.setForeground(Color.BLACK);
                        chatEscogido = chats.get(finalI);
                    }
                });
            }
            chatEscogido = null;
            mainFrame.revalidate();
            mainFrame.repaint();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

