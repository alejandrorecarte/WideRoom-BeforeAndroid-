package controllers.frameControllers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import controllers.Encoding;
import okhttp3.*;
import org.json.JSONObject;
import java.io.OutputStreamWriter;

public class RegisterFrame {
    private static final String FIREBASE_API_KEY = "AIzaSyDW3T_5QVO6MWPVpINQda0sBEqWauMSVm8";
    private JPanel mainPanel;
    private JLabel wideRoomLabel;
    private JLabel emailLabel;
    private JTextField emailField;
    private JPasswordField contraseñaField;
    private JLabel contraseñaLabel;
    private JButton registerButton;
    private JPasswordField repetirContraseñaField;
    private JLabel repetirContraseñaLabel;
    private JTextField nombreDeUsuarioField;
    private JLabel nombreDeUsuarioLabel;
    private JLabel statusLabel;
    private JButton volverButton;

    public static void startUI(JFrame frame) {
        frame.setContentPane(new RegisterFrame(frame).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(0,0,500,450);
        frame.setIconImage(new ImageIcon("src/main/java/icons/LogoPlanoNoTitle.png").getImage());
        frame.setVisible(true);
    }

    public RegisterFrame(JFrame frame) {
        Image backIcon = new ImageIcon("src/main/java/icons/BackIcon.png").getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
        volverButton.setIcon(new ImageIcon(backIcon));

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LogInFrame.startUI(frame);
            }
        });

        nombreDeUsuarioField.requestFocus();
        Image image = new ImageIcon("src/main/java/icons/LogoBlanco.png").getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
        wideRoomLabel.setIcon(new ImageIcon(image));
        repetirContraseñaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                registerButton.doClick();
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(contraseñaField.getText().equals(repetirContraseñaField.getText()) && contraseñaField.getText().split("").length > 5
                        && emailField.getText().contains("@") && emailField.getText().contains(".")
                        && !nombreDeUsuarioField.getText().isEmpty() && !emailField.getText().isEmpty()
                        && !contraseñaField.getText().isEmpty() && !repetirContraseñaField.getText().isEmpty()) {
                    try {
                        String[] ids = registerUser(emailField.getText(), contraseñaField.getText());
                        sendEmailVerification(ids[0]);
                        exportUserData(ids[0], ids[1], nombreDeUsuarioField.getText());
                        controllers.frameControllers.LogInFrame.startUI(frame);
                    } catch (IOException e) {
                        e.printStackTrace();
                        statusLabel.setText("Error al registrar al usuario.");
                    }

                }else if(emailField.getText().contains(" ") && emailField.getText().split("").length >= 11){
                    statusLabel.setText("El nombre de usuario que quieres registrar no cumple con las siguientes condiciones:\n" +
                            "- No puede contener espacios.\n" +
                            "- No puede tener más de 10 caracteres.");
                } else if(!contraseñaField.getText().equals(repetirContraseñaField.getText())){
                    statusLabel.setText("Los campos de contraseñas no coinciden.");
                }else if(contraseñaField.getText().split("").length <= 5){
                    statusLabel.setText("La contraseña debe tener al menos 6 dígitos.");
                }else if (nombreDeUsuarioField.getText().isEmpty() && emailField.getText().isEmpty()
                        && contraseñaField.getText().isEmpty() && repetirContraseñaField.getText().isEmpty()){
                    statusLabel.setText("Los campos no deben estar vacíos.");
                }
            }
        });
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * TODO: Comprobación de errores 40*
     * @param email
     * @param password
     * @return {idToken, localId}
     * @throws IOException
     */

    private static String[] registerUser(String email, String password) throws IOException{
        OkHttpClient client = new OkHttpClient();

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="+FIREBASE_API_KEY;

        String jsonData = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        JSONObject jsonResponseObject = new JSONObject(response.body().string());
        String[] ids = {jsonResponseObject.getString("idToken"), jsonResponseObject.getString("localId")};
        return ids;
    }

    /**
     * Envía un correo de verificación al usuario.
     * TODO: Comprobación de errores 40*
     * @param idToken
     * @throws IOException
     */
    private static void sendEmailVerification(String idToken) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key="+FIREBASE_API_KEY;

        String jsonData = "{\"requestType\":\"VERIFY_EMAIL\",\"idToken\":\""+idToken+"\"}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
    }

    /**
     * Guarda el nombre de usuario en la BBDD.
     * TODO: Comprobación de errores 40*
     * @param idToken
     * @param localId
     * @param username
     * @throws IOException
     */
    private static void exportUserData(String idToken, String localId,  String username) throws IOException{
        OkHttpClient client = new OkHttpClient();

        // URL del endpoint para agregar datos a Firestore
        String url = "https://firestore.googleapis.com/v1/projects/wideroom-b6ed8/databases/(default)/documents/users/"+localId+"/data/username";

        String jsonData = "{\"fields\":{\"data\":{\"mapValue\":{\"fields\":{\"username\":{\"stringValue\":\"" + username + "\"}}}}}}";

        // Crea la solicitud POST con la autenticación adecuada
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + idToken)
                .addHeader("Content-Type", "application/json")
                .patch(body)
                .build();

        // Ejecuta la solicitud y maneja la respuesta
        Response response = client.newCall(request).execute();
    }
}
