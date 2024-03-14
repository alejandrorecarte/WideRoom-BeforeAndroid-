package controllers.frameControllers;

import controllers.Encoding;
import models.Servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddServerFrame {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    private JLabel IPLabel;
    private JLabel textPortLabel;
    private JLabel addANewServerLabel;
    private JTextField ipField;
    private JTextField textPortField;
    private JTextField imagePortSenderField;
    private JTextField imagePortReceiverField;
    private JPasswordField passwordField;
    private JButton addButton;
    private JLabel imagePortSenderLabel;
    private JLabel imagePortReceiverLabel;
    private JLabel passwordLabel;
    private JPanel mainPanel;
    private JTextField serverNameField;
    private JLabel serverNameLabel;
    private JButton volverButton;

    public static void startUI(JFrame frame) {
        frame.setContentPane(new AddServerFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(new ImageIcon("src/main/java/icons/LogoPlanoNoTitle.png").getImage());
        frame.setBounds(frame.getX(), frame.getY(), WIDTH, HEIGHT);
        frame.setVisible(true);
    }

    public AddServerFrame() {
        Image backIcon = new ImageIcon("src/main/java/icons/BackIcon.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        volverButton.setIcon(new ImageIcon(backIcon));

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MainFrame.startUI(MainFrame.user, MainFrame.servidores);
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Servidor servidor = new Servidor(serverNameField.getText(), ipField.getText(), Integer.valueOf(textPortField.getText()),
                        Integer.valueOf(imagePortSenderField.getText()), Integer.valueOf(imagePortReceiverField.getText()),
                        Encoding.hashPassword(passwordField.getText()));
                MainFrame.servidores.add(servidor);

                MainFrame.startUI(MainFrame.user, MainFrame.servidores);
            }
        });
    }
}
