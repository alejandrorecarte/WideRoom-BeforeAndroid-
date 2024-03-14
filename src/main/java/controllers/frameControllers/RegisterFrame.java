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
                        if(isUserRegistered(emailField.getText(), nombreDeUsuarioField.getText())) {
                            // Construir la URL para el registro de un nuevo usuario
                            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;

                            // Crear el cuerpo de la solicitud JSON
                            String requestBody = "{\"email\":\"" + emailField.getText() + "\",\"password\":\"" + contraseñaField.getText() + "\",\"returnSecureToken\":true}";

                            // Enviar la solicitud POST para registrar un nuevo usuario
                            String response = sendPostRequest(url, requestBody);

                            // Si el registro es exitoso, muestra un mensaje y procede
                            JSONObject jsonResponseObject = new JSONObject(response);
                            String idToken = jsonResponseObject.getString("idToken");
                            sendEmailVerification(idToken);
                            exportUserData(emailField.getText(), nombreDeUsuarioField.getText());
                            addServidoresList(emailField.getText());
                            controllers.frameControllers.LogInFrame.startUI(frame);
                        }else{
                            statusLabel.setText("Nombre de usuario ya registrado.");
                        }
                    } catch (Exception e) {
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

    private static String sendPostRequest(String url, String requestBody) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Escribir el cuerpo de la solicitud en el flujo de salida
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.writeBytes(requestBody);
            outputStream.flush();
        }

        // Leer la respuesta del servidor
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

    private static void sendEmailVerification(String idtoken) throws IOException {
        // Construir la URL para enviar la solicitud de verificación del correo electrónico
        String verificationUrl = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" + FIREBASE_API_KEY;

        // Crear el cuerpo de la solicitud JSON para enviar la solicitud de verificación
        String verificationRequestBody = "{\"requestType\":\"VERIFY_EMAIL\",\"idToken\":\"" + idtoken + "\"}";

        // Enviar la solicitud POST para enviar la solicitud de verificación del correo electrónico
        String verificationResponse = sendPostRequest(verificationUrl, verificationRequestBody);
    }
;
    private static void exportUserData(String email, String username) throws Exception{
        // URL de la API REST de Cloud Firestore
        String firestoreUrl = "https://firestore.googleapis.com/v1/projects/wideroom-b6ed8/databases/(default)/documents/users";

        // Construir el JSON con los datos del usuario
        String jsonData = "{\"fields\":{\"email\":{\"stringValue\":\"" + Encoding.hashPassword(email) + "\"},\"username\":{\"stringValue\":\"" + username + "\"}}}";

        // Crear la conexión HTTP
        URL url = new URL(firestoreUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Escribir los datos en la conexión
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(jsonData);
        writer.flush();
        writer.close();

        // Verificar la respuesta del servidor
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            System.out.println("Error al agregar datos al documento en Cloud Firestore. Código de respuesta: " + responseCode);
        }
    }

    private static boolean isUserRegistered(String username, String email) throws Exception {
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
            if(line.contains(email) || line.contains(username)){
                return false;
            }
        }
        reader.close();

        // Verificar la respuesta del servidor
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
        } else {
            System.out.println("Error al recuperar los documentos. Código de respuesta: " + responseCode);
        }
        return true;
    }

    private static void addServidoresList(String email) throws Exception {
        // Construir el JSON con los datos del usuario y la lista de servidores
        String hashedEmail = Encoding.hashPassword(email);
        StringBuilder jsonDataBuilder = new StringBuilder();
        jsonDataBuilder.append("{\"fields\":{\"email\":{\"stringValue\":\"").append(hashedEmail).append("\"},\"servers\":{\"arrayValue\":{\"values\":[");

        jsonDataBuilder.append("{\"mapValue\":{\"fields\":{");
        jsonDataBuilder.append("\"name\":{\"stringValue\":\"").append(Encoding.encrypt("Localhost", hashedEmail)).append("\"},");
        jsonDataBuilder.append("\"ip\":{\"stringValue\":\"").append(Encoding.encrypt("localhost", hashedEmail)).append("\"},");
        jsonDataBuilder.append("\"textPort\":{\"stringValue\":\"").append(Encoding.encrypt("5555", hashedEmail)).append("\"},");
        jsonDataBuilder.append("\"imagePortSender\":{\"stringValue\":\"").append(Encoding.encrypt("2020", hashedEmail)).append("\"},");
        jsonDataBuilder.append("\"imagePortReceiver\":{\"stringValue\":\"").append(Encoding.encrypt("2021", hashedEmail)).append("\"},");
        jsonDataBuilder.append("\"hashedPassword\":{\"stringValue\":\"").append(Encoding.encrypt("", hashedEmail)).append("\"}");
        jsonDataBuilder.append("}}}");

        jsonDataBuilder.append("]}}}}");

        String jsonData = jsonDataBuilder.toString();

        // URL de la API REST de Cloud Firestore para agregar un nuevo documento
        String firestoreUrl = "https://firestore.googleapis.com/v1/projects/wideroom-b6ed8/databases/(default)/documents/serverslist";

        // Crear la conexión HTTP
        URL url = new URL(firestoreUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Escribir los datos en la conexión
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
            writer.write(jsonData);
        }

        // Verificar la respuesta del servidor
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            System.out.println("Error al agregar datos al documento en Cloud Firestore. Código de respuesta: " + responseCode);
        } else {
            System.out.println("Datos agregados correctamente a Cloud Firestore.");
        }
    }
}
