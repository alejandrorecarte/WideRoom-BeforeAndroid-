package controllers.frameControllers;

import controllers.Encoding;
import controllers.Streams;
import models.User;
import okhttp3.*;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

public class LogInFrame {

    private static final String FIREBASE_API_KEY = "AIzaSyDW3T_5QVO6MWPVpINQda0sBEqWauMSVm8";
    public static JFrame frame;
    private JPanel mainPanel;
    private JTextField emailField;
    private JLabel emailLabel;
    private JLabel contraseñaLabel;
    private JPasswordField contraseñaField;
    private JButton iniciarSesiónButton;
    private JLabel wideRoomLabel;
    private JButton noTienesUnaCuentaButton;
    private JButton noRecuerdasTuContraseñaButton;
    private JLabel statusLabel;
    private JCheckBox recordarEmailCheckBox;

    public static void startUI() {
        frame = new JFrame("WideRoom");
        frame.setContentPane(new LogInFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(0, 0, 500, 450);
        frame.setIconImage(new ImageIcon("src/main/java/icons/LogoPlanoNoTitle.png").getImage());
        frame.setVisible(true);
    }

    public static void startUI(JFrame frame) {
        frame.setContentPane(new LogInFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(0, 0, 500, 450);
        frame.setIconImage(new ImageIcon("src/main/java/icons/LogoPlanoNoTitle.png").getImage());
        frame.setVisible(true);
    }

    public LogInFrame() {
        try{
            emailField.setText(Streams.importarEmail());
            recordarEmailCheckBox.setSelected(true);
        }catch (Exception e){
        }

        Image image = new ImageIcon("src/main/java/icons/LogoBlanco.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        wideRoomLabel.setIcon(new ImageIcon(image));
        contraseñaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                iniciarSesiónButton.doClick();
            }
        });
        noTienesUnaCuentaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                controllers.frameControllers.RegisterFrame.startUI(frame);
            }
        });

        iniciarSesiónButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    User user = logIn(emailField.getText(), contraseñaField.getText());
                    if (!checkIfAccountIsVerified(user)) {
                        statusLabel.setText("La cuenta no se ha verificado, comprueba tu email.");
                    } else {
                        //Si no da error el inicio de sesión es correcto
                        if(recordarEmailCheckBox.isSelected()){
                            Streams.exportarEmail(emailField.getText());
                        }else{
                            File email = new File("src/main/java/resources/email");
                            email.delete();
                        }
                        controllers.frameControllers.MainFrame.startUI(user);
                        frame.dispose();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Error al iniciar sesión, comprueba las credenciales.");
                }
            }
        });

        noRecuerdasTuContraseñaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    sendPasswordChange(emailField.getText());
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        try{
            User user = logIn("alex.rekart3@gmail.com", "123456");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Permite iniciar sesión
     * @param email
     * @param password
     * @return User user
     * @throws IOException
     */
    private static User logIn(String email, String password) throws IOException{
        OkHttpClient client = new OkHttpClient();

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="+FIREBASE_API_KEY;

        String jsonData = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        JSONObject jsonResponseObject = new JSONObject(response.body().string());

        String username = getUsername(jsonResponseObject.getString("idToken"),jsonResponseObject.getString("localId"));
        return new User(jsonResponseObject.getString("idToken"), jsonResponseObject.getString("localId"), jsonResponseObject.getString("email"), username);
    }

    /**
     * Devuelve el nombre del usuario
     * @param idToken
     * @param localId
     * @return String username
     * @throws IOException
     */
    private static String getUsername(String idToken, String localId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = "https://firestore.googleapis.com/v1/projects/wideroom-b6ed8/databases/(default)/documents/users/"+localId+"/data/username";


        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + idToken)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        JSONObject jsonResponseObject = new JSONObject(response.body().string());
        JSONObject fieldsObject = jsonResponseObject.getJSONObject("fields");
        JSONObject dataObject = fieldsObject.getJSONObject("data");
        JSONObject usernameObject = dataObject.getJSONObject("mapValue").getJSONObject("fields").getJSONObject("username");
        return usernameObject.getString("stringValue");
    }

    /**
     * Comprueba si la cuenta ha sido verificada
     * @param user
     * @return boolean isVerified
     * @throws IOException
     */
    private static boolean checkIfAccountIsVerified(User user) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:update?key="+FIREBASE_API_KEY;

        String jsonData = "{\"requestType\":\"VERIFY_EMAIL\",\"idToken\":\"" + user.getIdToken() + "\"}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        JSONObject jsonResponseObject = new JSONObject(response.body().string());

        return jsonResponseObject.getBoolean("emailVerified");
    }

    /**
     * Envía un correo para cambiar la contraseña del usuario
     * @param email
     * @throws IOException
     */
    private void sendPasswordChange(String email) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key="+FIREBASE_API_KEY;

        String jsonData = "{\"requestType\":\"PASSWORD_RESET\",\"email\":\"" + email + "\"}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
    }
}
