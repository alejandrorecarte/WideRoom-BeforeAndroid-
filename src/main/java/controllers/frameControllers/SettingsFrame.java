package controllers.frameControllers;

import controllers.Streams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsFrame {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    private static JFrame frame;
    private JPanel mainPanel;
    private JLabel filesDownloadsClientPathLabel;
    private JTextField filesDownloadsClientPathField;
    private JLabel clientSettingsLabel;
    private JTextField filesDownloadsServerPathField;
    private JLabel filesDownloadsServerPathLabel;
    private JLabel wideRoomSettingsLabel;
    private JLabel serverSettingsLabel;
    private JLabel textPortServerLabel;
    private JTextField textPortServerField;
    private JTextField imagePortSenderServerField;
    private JTextField imagePortReceiverServerField;
    private JLabel imagePortSenderServerLabel;
    private JLabel imagePortReceiverServerLabel;
    private JTextField textPortClientField;
    private JTextField imagePortSenderClientField;
    private JTextField imagePortReceiverClientField;
    private JCheckBox autodestroyImagesServerCheckBox;
    private JCheckBox autodestroyImagesClientCheckBox;
    private JButton volverButton;

    public static void startUI(JFrame frame) {
        frame.setContentPane(new SettingsFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setBounds(0,0, WIDTH, HEIGHT);
        frame.setIconImage(new ImageIcon("src/main/java/icons/LogoPlanoNoTitle.png").getImage());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public SettingsFrame() {
        Image backIcon = new ImageIcon("src/main/java/icons/BackIcon.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        volverButton.setIcon(new ImageIcon(backIcon));

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Streams.exportarFilesDownloadsServerPath(filesDownloadsServerPathField.getText());
                    Streams.exportarFilesDownloadsClientPath(filesDownloadsClientPathField.getText());
                    Streams.exportarTextPortServer(Integer.parseInt(textPortServerField.getText()));
                    Streams.exportarImagePortSenderServer(Integer.parseInt(imagePortSenderServerField.getText()));
                    Streams.exportarImagePortReceiverServer(Integer.parseInt(imagePortReceiverServerField.getText()));
                    Streams.exportarAutodestroyImagesClient(autodestroyImagesClientCheckBox.isSelected());
                    Streams.exportarAutodestroyImagesServer(autodestroyImagesServerCheckBox.isSelected());
                }catch(Exception ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error", "Error exporting settings infromation", JOptionPane.ERROR_MESSAGE);
                    frame.dispose();
                }
                MainFrame.startUI(MainFrame.user, MainFrame.servidores);
            }
        });

        try{
            filesDownloadsServerPathField.setText(Streams.importarFilesDownloadsServerPath());
            filesDownloadsClientPathField.setText(Streams.importarFilesDownloadsClientPath());
            textPortServerField.setText(String.valueOf(Streams.importarTextPortServer()));
            imagePortSenderServerField.setText(String.valueOf(Streams.importarImagePortSenderServer()));
            imagePortReceiverServerField.setText(String.valueOf(Streams.importarImagePortReceiverServer()));
            if(Streams.importarAutodestroyImagesClient()) {
                autodestroyImagesClientCheckBox.setSelected(true);
            }
            if(Streams.importarAutodestroyImagesServer()) {
                autodestroyImagesServerCheckBox.setSelected(true);
            }
        }catch(Exception e){
            filesDownloadsServerPathField.setText("src/files/server/");
            filesDownloadsClientPathField.setText("src/files/client/");
            textPortServerField.setText("5555");
            imagePortSenderServerField.setText("2021");
            imagePortReceiverServerField.setText("2020");
            autodestroyImagesClientCheckBox.setSelected(true);
            autodestroyImagesServerCheckBox.setSelected(true);
        }
    }
}