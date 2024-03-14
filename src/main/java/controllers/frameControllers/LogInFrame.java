package controllers.frameControllers;

import controllers.Encoding;
import controllers.Streams;
import models.Servidor;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
                    String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;

                    String requestBody = "{\"email\":\"" + emailField.getText() + "\",\"password\":\"" + contraseñaField.getText() + "\",\"returnSecureToken\":true}";

                    String response = sendPostRequest(url, requestBody);

                    JSONObject jsonResponseObject = new JSONObject(response);

                    String idToken = jsonResponseObject.getString("idToken");

                    if (!checkIfAccountIsVerified(idToken)) {
                        statusLabel.setText("La cuenta no se ha verificado, comprueba tu email.");
                    } else {
                        //Si no da error el inicio de sesión es correcto
                        User user = new User(emailField.getText(), Encoding.hashPassword(contraseñaField.getText()), getUsername(emailField.getText()));

                        if(recordarEmailCheckBox.isSelected()){
                            Streams.exportarEmail(emailField.getText());
                        }else{
                            File email = new File("src/main/java/resources/email");
                            email.delete();
                        }
                        controllers.frameControllers.MainFrame.startUI(user, getServidoresList(emailField.getText()));
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
                sendPasswordChange(emailField.getText());
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

    private boolean checkIfAccountIsVerified(String idToken) {
        try {
            // Construir la URL para enviar la solicitud de verificación del correo electrónico
            String verificationUrl = "https://identitytoolkit.googleapis.com/v1/accounts:update?key=" + FIREBASE_API_KEY;

            // Crear el cuerpo de la solicitud JSON para enviar la solicitud de verificación
            String verificationRequestBody = "{\"requestType\":\"VERIFY_EMAIL\",\"idToken\":\"" + idToken + "\"}";

            // Enviar la solicitud POST para enviar la solicitud de verificación del correo electrónico
            String verificationResponse = sendPostRequest(verificationUrl, verificationRequestBody);

            JSONObject jsonResponseObject = new JSONObject(verificationResponse);

            return jsonResponseObject.getBoolean("emailVerified");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getUsername(String email) throws Exception {
        // URL de la API REST de Cloud Firestore para obtener todos los documentos de la colección "users"
        String firestoreUrl = "https://firestore.googleapis.com/v1/projects/wideroom-b6ed8/databases/(default)/documents/users";

        // Crear la conexión HTTP
        URL url = new URL(firestoreUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Leer la respuesta del servidor
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Convertir la respuesta JSON en un objeto JSONObject
        JSONObject jsonResponse = new JSONObject(response.toString());

        // Obtener la lista de documentos de la respuesta JSON
        JSONArray documents = jsonResponse.getJSONArray("documents");

        // Iterar sobre cada documento
        for (int i = 0; i < documents.length(); i++) {
            // Obtener el contenido del documento actual
            JSONObject document = documents.getJSONObject(i);

            // Obtener el contenido del campo "fields" del documento
            JSONObject fields = document.getJSONObject("fields");

            // Obtener el valor del campo "email" del documento
            String userEmail = fields.getJSONObject("email").getString("stringValue");

            // Obtener el valor del campo "username" del documento
            String username = fields.getJSONObject("username").getString("stringValue");

            try {
                // Verificar si el "email" coincide con el correo electrónico proporcionado
                if ((Encoding.hashPassword(email).equals(userEmail))) {
                    return username;
                }
            } catch (Exception e) {
            }
        }

        // Si no se encontró el usuario
        return null;
    }

    private void sendPasswordChange(String email) {
        try {
            String verificationUrl = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" + FIREBASE_API_KEY;

            // Crear el cuerpo de la solicitud JSON para enviar la solicitud de verificación
            String verificationRequestBody = "{\"requestType\":\"PASSWORD_RESET\",\"email\":\"" + email + "\"}";

            // Enviar la solicitud POST para enviar la solicitud de verificación del correo electrónico
            String verificationResponse = sendPostRequest(verificationUrl, verificationRequestBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Servidor> getServidoresList(String email) throws Exception {
        String hashedEmail = Encoding.hashPassword(email);
        // URL de la API REST de Cloud Firestore para obtener todos los documentos
        String getUrl = "https://firestore.googleapis.com/v1/projects/wideroom-b6ed8/databases/(default)/documents/serverslist";

        // Realizar una solicitud GET para obtener todos los documentos
        URL getEndpoint = new URL(getUrl);
        HttpURLConnection getConnection = (HttpURLConnection) getEndpoint.openConnection();
        getConnection.setRequestMethod("GET");

        // Leer la respuesta
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getConnection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        // Parsear la respuesta para encontrar el documento que coincide con el correo electrónico
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray documents = jsonResponse.getJSONArray("documents");
        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            JSONObject fields = document.getJSONObject("fields");
            if (fields.has("email")) {
                String documentEmail = fields.getJSONObject("email").getString("stringValue");
                if (documentEmail.equals(hashedEmail)) {
                    // Encontramos el documento que coincide con el correo electrónico, extraer los servidores
                    JSONArray serversArray = fields.getJSONObject("servers").getJSONObject("arrayValue").getJSONArray("values");
                    ArrayList<Servidor> servidores = new ArrayList<>();
                    for (int j = 0; j < serversArray.length(); j++) {
                        JSONObject serverObject = serversArray.getJSONObject(j).getJSONObject("mapValue").getJSONObject("fields");
                        String name = Encoding.decrypt(serverObject.getJSONObject("name").getString("stringValue"), hashedEmail);
                        String ip = Encoding.decrypt(serverObject.getJSONObject("ip").getString("stringValue"), hashedEmail);
                        int textPort = Integer.valueOf(Encoding.decrypt(serverObject.getJSONObject("textPort").getString("stringValue"), hashedEmail));
                        int imagePortSender = Integer.valueOf(Encoding.decrypt(serverObject.getJSONObject("imagePortSender").getString("stringValue"), hashedEmail));
                        int imagePortReceiver = Integer.valueOf(Encoding.decrypt(serverObject.getJSONObject("imagePortReceiver").getString("stringValue"), hashedEmail));
                        String hashedPassword = Encoding.decrypt(serverObject.getJSONObject("hashedPassword").getString("stringValue"), hashedEmail);
                        servidores.add(new Servidor(name, ip, textPort, imagePortSender, imagePortReceiver, hashedPassword));
                    }
                    return servidores;
                }
            }
        }

        // No se encontró un documento para el correo electrónico proporcionado
        System.out.println("No se encontró un documento para el correo electrónico proporcionado.");
        return null;
    }
}
