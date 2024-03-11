package controllers.frameControllers;

import javax.swing.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import java.io.FileInputStream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame {
    public static JFrame frame;
    private JPanel mainPanel;
    private JLabel wideRoomLabel;
    private JLabel emailLabel;
    private JTextField emailField;
    private JPasswordField contraseñaField;
    private JLabel contraseñaLabel;
    private JButton registerButton;
    private FirebaseAuth mAuth;

    public static void startUI() {
        frame = new JFrame("WideRoom");
        frame.setContentPane(new RegisterFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(0,0,800,800);
        frame.setVisible(true);
    }

    public RegisterFrame() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    FileInputStream serviceAccount =
                            new FileInputStream("C:\\Users\\alexr\\Desktop\\wideroom-b6ed8-firebase-adminsdk-3ke8f-fc3f76e378.json");

                    FirebaseOptions options = new FirebaseOptions.Builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .setDatabaseUrl("https://wideroom-b6ed8.firebaseio.com/")
                            .build();

                    FirebaseApp.initializeApp(options);

                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    try {
                        auth.createUser(new UserRecord.CreateRequest()
                                .setEmail(emailField.getText())
                                .setPassword(contraseñaField.getText()));
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(frame, "Usuario registrado correctamente", "Confirmación", JOptionPane.INFORMATION_MESSAGE);
                    LogInFrame.startUI();
                    frame.dispose();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
