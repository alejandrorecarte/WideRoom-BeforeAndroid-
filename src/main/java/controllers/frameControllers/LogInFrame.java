package controllers.frameControllers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class LogInFrame {
    public static JFrame frame;
    private JPanel mainPanel;
    private JTextField emailField;
    private JLabel emailLabel;
    private JLabel contraseñaLabel;
    private JPasswordField contraseñaField;
    private JButton iniciarSesiónButton;
    private JLabel wideRoomLabel;
    private JButton noTienesUnaCuentaButton;

    public static void startUI() {
        frame = new JFrame("WideRoom");
        frame.setContentPane(new LogInFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(0,0, 800,800);
        frame.setVisible(true);
    }

    public LogInFrame() {
        noTienesUnaCuentaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                controllers.frameControllers.RegisterFrame.startUI();
                frame.dispose();
            }
        });

        iniciarSesiónButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String firebaseApiKey = "AIzaSyDW3T_5QVO6MWPVpINQda0sBEqWauMSVm8";
                    String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseApiKey;

                    String requestBody = "{\"email\":\"" + emailField.getText() + "\",\"password\":\"" + contraseñaField.getText() + "\",\"returnSecureToken\":true}";

                    sendPostRequest(url, requestBody);

                    //Si no da error el inicio de sesión es correcto
                    controllers.frameControllers.MainFrame.startUI();
                    frame.dispose();
                }catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error al iniciar sesión.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private static String sendPostRequest(String url, String requestBody) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Escribe el cuerpo de la solicitud
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.writeBytes(requestBody);
            outputStream.flush();
        }

        // Lee la respuesta
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }
}
