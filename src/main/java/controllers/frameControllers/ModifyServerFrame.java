package controllers.frameControllers;

import controllers.Encoding;
import models.Servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModifyServerFrame {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    private static JFrame frame;
    private JPanel mainPanel;
    private JLabel addANewServerLabel;
    private JLabel IPLabel;
    private JLabel textPortLabel;
    private JLabel imagePortSenderLabel;
    private JLabel imagePortReceiverLabel;
    private JTextField ipField;
    private JTextField textPortField;
    private JTextField imagePortSenderField;
    private JTextField imagePortReceiverField;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JButton modifyButton;
    private JTextField serverNameField;
    private JLabel serverNameLabel;
    private JButton volverButton;

    public static void startUI(JFrame frame, Servidor servidor) {
        frame.setContentPane(new ModifyServerFrame(servidor).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(MainFrame.mainFrame.getX(), MainFrame.mainFrame.getY(), 500, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(new ImageIcon("src/main/java/icons/LogoPlanoNoTitle.png").getImage());
        frame.setBounds(frame.getX(), frame.getY(), WIDTH, HEIGHT);
        frame.setVisible(true);
    }

    public ModifyServerFrame(Servidor servidor) {
        Image backIcon = new ImageIcon("src/main/java/icons/BackIcon.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        volverButton.setIcon(new ImageIcon(backIcon));

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MainFrame.startUI(MainFrame.user, MainFrame.servidores);
            }
        });

        serverNameField.setText(servidor.getName());
        ipField.setText(servidor.getIp());
        textPortField.setText(String.valueOf(servidor.getTextPort()));
        imagePortSenderField.setText(String.valueOf(servidor.getImagePortSender()));
        imagePortReceiverField.setText(String.valueOf(servidor.getImagePortReceiver()));


        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.servidores.remove(servidor);
                Servidor servidor = new Servidor(serverNameField.getText(), ipField.getText(), Integer.valueOf(textPortField.getText()),
                        Integer.valueOf(imagePortSenderField.getText()), Integer.valueOf(imagePortReceiverField.getText()),
                        Encoding.hashPassword(passwordField.getText()));
                MainFrame.servidores.add(servidor);

                MainFrame.startUI(MainFrame.user, MainFrame.servidores);
            }
        });
    }
}
