package controllers.frameControllers;

import controllers.Misc;
import controllers.Streams;
import views.components.ImageChooserComponent;
import views.components.MessagePanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import static controllers.Encoding.decrypt;
import static controllers.Encoding.encrypt;
import static controllers.frameControllers.MainFrame.*;

public class JoinServerFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 1000;
    public static JFrame clientFrame;
    public JPanel mainPanel;
    private JLabel wideRoomClientLabel;
    private JButton sendButton;
    private JTextField chatInputField;
    private JScrollPane chatOutputScrollPane;
    private ImageChooserComponent imageChooserComponent;
    private JPanel messagesPanel;
    public static String username;
    private static LinkedList<String> messages;
    private boolean access;
    private SwingWorker<Void, Void> worker;
    private PrintWriter writer;

    public static void startUI(String username, PrintWriter writer) {
        clientFrame = new JFrame("WideRoom Client");
        clientFrame.setContentPane(new JoinServerFrame(username, writer).mainPanel);
        clientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        clientFrame.pack();
        clientFrame.setIconImage(new ImageIcon("src/main/java/icons/LogoPlanoNoTitle.png").getImage());
        clientFrame.setBounds(mainFrame.getX() + MainFrame.WIDTH, mainFrame.getY() , WIDTH, HEIGHT);
    }

    private void createUIComponents() {
        this.imageChooserComponent = new ImageChooserComponent();
    }

    public JoinServerFrame(String username, PrintWriter writer) {
        /*messages = new LinkedList<String>();
        this.username = username;
        this.writer = writer;
        messagesPanel.setLayout(new GridBagLayout());
        chatOutputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatOutputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        chatOutputScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        chatOutputScrollPane.getVerticalScrollBar().setBackground(Color.decode("#4F4F4F"));
        access = false;
        messages  = new LinkedList<String>();

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkPassword()) {
                    new Handler().start();
                }else{
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();

        sendMessage("/requestHashedPassword", writer);

        sendButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!chatInputField.getText().equals("")) {
                    sendMessage(writer);
                    chatInputField.setText("");
                }
                if(imageChooserComponent.getPath()!=null){
                    writer.println(encrypt(username + ":" +Misc.getDate() +":"  + username + " sent an image.", MainFrame.clientHashedPassword));
                    MainFrame.clientMessages.add(encrypt(username + ": "  + Misc.getDate() +":"  +  username + " sent an image.", MainFrame.clientHashedPassword));
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try (Socket imageSocket = new Socket(MainFrame.joinIP, MainFrame.servidorEscogido.getImagePortSender());
                         OutputStream outputStream = imageSocket.getOutputStream();
                         FileInputStream fileInputStream = new FileInputStream(imageChooserComponent.getPath())) {

                        Thread.sleep(100);

                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    imageChooserComponent.setPath(null);
                    imageChooserComponent.getChooseButton().setText("Seleccionar imagen");
                    imageChooserComponent.getChooseButton().setBackground(Color.decode("#272727"));
                    imageChooserComponent.getChooseButton().setForeground(Color.WHITE);
                }
            }
        });

        imageChooserComponent.getChooseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatInputField.requestFocus();
            }
        });

        chatInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendButton.doClick();
                }
            }
        });

       clientFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(clientFrame, "Do you want to exit this chat server?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    sendMessage(encrypt("Server:" + Misc.getDate() +":"  + username + " left the server.", MainFrame.clientHashedPassword), writer);
                    clientFrame.dispose();
                    timer.stop();

                } else {
                    clientFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

         */
    }

    private void sendMessage(PrintWriter writer){

    }

    public synchronized void actualizarChat(){
        /*
        if (JoinServerFrame.messages.size() < (MainFrame.clientMessages.size())) {
            try {

                JScrollBar verticalScrollBar = chatOutputScrollPane.getVerticalScrollBar();
                boolean keepBottom = false;
                double oldValue = verticalScrollBar.getSize().getHeight() + verticalScrollBar.getValue();
                if(oldValue >= verticalScrollBar.getMaximum() || verticalScrollBar.getSize().getHeight() == 0){
                    keepBottom = true;
                }

                JoinServerFrame.messages.add(MainFrame.clientMessages.getLast());
                String encryptedMessage = JoinServerFrame.messages.getLast();
                String decryptedMessage = decrypt(encryptedMessage, MainFrame.clientHashedPassword);
                String username;
                String date;
                String message;
                System.out.println(decryptedMessage);
                try {
                    message = decryptedMessage.split(":")[3];
                    date = decryptedMessage.split(":")[1] + ":" + decryptedMessage.split(":")[2] ;
                    username = decryptedMessage.split(":")[0];
                } catch (Exception e){
                    message = decryptedMessage;
                    date="null";
                    username = "null";
                }

                MessagePanel beforeMessagePanel = null;

                try {
                    beforeMessagePanel = ((MessagePanel) messagesPanel.getComponent(messagesPanel.getComponentCount()-1));
                }catch (ArrayIndexOutOfBoundsException ex) {}

                //Si el mensaje lo envía el mismo usuario que el anterior lo anida en el mismo panel
                if(beforeMessagePanel != null && beforeMessagePanel.getUsername().equals(username) || username.equals("Local")) {
                    //Lo anida
                    beforeMessagePanel.addMessage(message);

                    //Comprueba si la fecha coincide con la última fecha impresa
                    if(beforeMessagePanel.getMessageLabels().get(beforeMessagePanel.getMessageLabels().size()-2).getText().equals(date)){
                        beforeMessagePanel.getMessagesPanel().remove(beforeMessagePanel.getMessageLabels().size()-2);
                        beforeMessagePanel.getMessageLabels().remove(beforeMessagePanel.getMessageLabels().size()-2);
                        beforeMessagePanel.addDate(date);
                    }else{
                        beforeMessagePanel.addDate(date);
                    }

                    //Comprobación por si es una imagen
                    if (message.contains("File saved into")) {
                        ImageIcon image = new ImageIcon(message.split(" ")[3]);
                        Image imageScaled = image.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        ImageIcon scaledImageIcon = new ImageIcon(imageScaled);
                        beforeMessagePanel.setToolTipText(beforeMessagePanel.getMessageLabels().get(beforeMessagePanel.getMessageLabels().size()-1).getText());
                        beforeMessagePanel.getMessageLabels().get(beforeMessagePanel.getMessageLabels().size()-2).setText("");
                        beforeMessagePanel.getMessageLabels().get(beforeMessagePanel.getMessageLabels().size()-2).setIcon(scaledImageIcon);
                        beforeMessagePanel.getMessageLabels().get(beforeMessagePanel.getMessageLabels().size()-2).repaint();
                    }
                }
                else{
                    MessagePanel messagePanel = new MessagePanel(username, message, date,Color.decode("#141414"), Color.WHITE);
                    messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    if (username.equals("Server")) {
                        messagePanel.setColor(Color.decode("#f14c8e"), Color.BLACK);
                    }
                    if (username.equals(this.username)) {
                        messagePanel.setColor(Color.decode("#000000"), Color.WHITE);
                    }

                    if (username.equals(this.username) || (message.contains("File saved into") && username.equals(this.username))) {
                        messagesPanel.add(messagePanel, new GridBagConstraints(0, messagesPanel.getComponentCount(), 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.CENTER,
                                new Insets(3, 3, 10, 3), 0, 0));
                    } else {
                        messagesPanel.add(messagePanel, new GridBagConstraints(0, messagesPanel.getComponentCount(), 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.CENTER,
                                new Insets(3, 10, 3, 3), 0, 0));
                    }
                }
                clientFrame.revalidate();
                clientFrame.repaint();

                try {
                    if (decrypt(MainFrame.clientMessages.getLast(), MainFrame.clientHashedPassword).split(" ")[3].equals("sent")) {
                        try(ServerSocket imageSocketServer = new ServerSocket(MainFrame.servidorEscogido.getImagePortReceiver())) {
                            Socket imageSocket = imageSocketServer.accept();
                            Thread handlerThread = new Thread(new ImageConnectionHandler(imageSocket, decrypt(MainFrame.clientMessages.getLast(), MainFrame.clientHashedPassword).split(":")[0]));
                            handlerThread.start();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }catch(Exception e){
                }

                Thread.sleep(100);

                if(keepBottom) {
                    SwingUtilities.invokeLater(() -> {
                        JScrollBar newVerticalScrollBar = chatOutputScrollPane.getVerticalScrollBar();
                        newVerticalScrollBar.setValue(newVerticalScrollBar.getMaximum());
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

         */
    }
}
