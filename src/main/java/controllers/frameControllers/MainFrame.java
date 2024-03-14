package controllers.frameControllers;

import controllers.Encoding;
import controllers.Misc;
import controllers.Streams;
import controllers.handlers.HandlerHostServer;
import models.Servidor;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.*;

import static controllers.Encoding.*;

public class MainFrame {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    public static JFrame mainFrame;
    private JPanel hostPanel;
    private JPanel joinPanel;
    private JLabel hostAChatServerLabel;
    private JTextField hostIpField;
    private JLabel hostPasswordLabel;
    private JButton createServerButton;
    private JLabel joinAServerLabel;
    private JPasswordField hostPasswordField;
    private JButton joinServerButton;
    private JPanel mainPanel;
    private JComboBox profilesComboBox;
    private JLabel wideRoomLabel;
    private JButton settingsButton;
    private JButton addServerButton;
    private JScrollPane serversScrollPane;
    private JPanel serversPanel;
    private JButton removeServerButton;
    private JButton modifyServerButton;
    private JTextField usernameField;
    private JButton actualizarListaButton;
    private JLabel usernameLabel;
    public static LinkedList<String> serverMessages = new LinkedList<String>();
    public static LinkedList<String> clientMessages = new LinkedList<String>();
    private static final ArrayList<PrintWriter> writers = new ArrayList<>();
    public static Socket clientSocket;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;
    private BufferedReader consoleReader;
    private ArrayList<String>[] profiles;
    public static String clientHashedPassword;
    public static String hostHashedPassword;
    public static String joinIP;
    private ServerSocket serverSocket;
    public static ArrayList<Servidor> servidores;
    public static Servidor servidorEscogido;
    public static User user;

    public static void startUI(User user,  ArrayList<Servidor> servidores) {
        mainFrame = new JFrame("WideRoom");
        mainFrame.setContentPane(new MainFrame(user, servidores).mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setIconImage(new ImageIcon("src/main/java/icons/LogoPlanoNoTitle.png").getImage());
        mainFrame.setBounds(0,0,WIDTH,HEIGHT);
    }

    public MainFrame(User user,  ArrayList<Servidor> servidores) {
        setButtonsEnabled(false, false, false);
        if(servidores != null) {
            this.servidores = servidores;
        }else this.servidores = new ArrayList<Servidor>();
        this.user = user;
        usernameLabel.setText("Bienvenid@ " + user.getUsername());
        Image icon = new ImageIcon("src/main/java/icons/LogoBlanco.png").getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
        wideRoomLabel.setIcon(new ImageIcon(icon));

        Image addIcon = new ImageIcon("src/main/java/icons/AddIcon.png").getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
        addServerButton.setIcon(new ImageIcon(addIcon));

        Image modifyIcon = new ImageIcon("src/main/java/icons/ModifyIcon.png").getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
        modifyServerButton.setIcon(new ImageIcon(modifyIcon));

        Image removeIcon = new ImageIcon("src/main/java/icons/RemoveIcon.png").getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
        removeServerButton.setIcon(new ImageIcon(removeIcon));

        Image refreshIcon = new ImageIcon("src/main/java/icons/RefreshIcon.png").getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
        actualizarListaButton.setIcon(new ImageIcon(refreshIcon));

        actualizarServerList();

        File filesDir = new File("src/files");
        if(!filesDir.exists()){
            filesDir.mkdir();
        }
        File clientDir = new File("src/files/client");
        if(!clientDir.exists()){
            clientDir.mkdir();
        }
        File serverDir = new File("src/files/server");
        if(!serverDir.exists()){
            serverDir.mkdir();
        }


        createServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!String.valueOf(hostPasswordField.getText()).equals("")) {
                    hostHashedPassword = hashPassword(hostPasswordField.getText());
                    controllers.frameControllers.HostServerFrame.startUI();

                    // Cerrar el servidor anterior si existe
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        try {
                            serverSocket.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    // Iniciar un nuevo servidor
                    startServer();
                }
            }
        });;

        joinServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    clientHashedPassword = servidorEscogido.getHashedPassword();
                    clientSocket = new Socket(servidorEscogido.getIp(), servidorEscogido.getTextPort());
                    clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                    consoleReader = new BufferedReader(new InputStreamReader(System.in));
                    controllers.frameControllers.JoinServerFrame.startUI(user.getUsername(), clientWriter);
                    clientMessages = new LinkedList<String>();
                    joinIP = servidorEscogido.getIp();
                    joinServer();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controllers.frameControllers.SettingsFrame.startUI(mainFrame);
            }
        });

        hostPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    createServerButton.doClick();
                }
            }
        });

        addServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddServerFrame.startUI(mainFrame);
                actualizarListaButton.setBackground(Color.decode("#f14c8e"));
            }
        });

        removeServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                servidores.remove(servidorEscogido);
                servidorEscogido = null;
                actualizarServerList();
            }
        });

        modifyServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(servidorEscogido != null){
                    ModifyServerFrame.startUI(mainFrame, servidorEscogido);
                    actualizarListaButton.setBackground(Color.decode("#f14c8e"));
                }
            }
        });

        actualizarListaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarServerList();
                actualizarListaButton.setBackground(Color.decode("#272727"));
            }
        });

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                try {
                    Misc.removeClientImages();
                    Misc.removeServerImages();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    public void actualizarServerList() {
        try {
            serversPanel.removeAll();
            serversPanel.setLayout(new BoxLayout(serversPanel, BoxLayout.Y_AXIS));
            for (int i = 0; i < servidores.size(); i++) {
                final int finalI = i;
                JButton b = new JButton(servidores.get(i).getName());
                serversPanel.add(b);
                b.setBackground(Color.decode("#272727"));
                b.setForeground(Color.WHITE);
                b.setFont(new Font("arial", Font.PLAIN, 16));
                b.setFocusPainted(false);
                b.setMaximumSize(new Dimension(Integer.MAX_VALUE, b.getPreferredSize().height));

                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for(int i = 0; i < serversPanel.getComponentCount(); i++){
                            serversPanel.getComponent(i).setBackground(Color.decode("#272727"));
                            serversPanel.getComponent(i).setForeground(Color.WHITE);
                        }
                        b.setBackground(Color.decode("#f14c8e"));
                        b.setForeground(Color.BLACK);
                        servidorEscogido = servidores.get(finalI);
                        setButtonsEnabled(true, true, true);
                    }
                });
            }
            setServidoresList(user.getEmail(), servidores);
            servidorEscogido = null;
            setButtonsEnabled(false, false, false);
            mainFrame.revalidate();
            mainFrame.repaint();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void startServer() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    serverSocket = new ServerSocket(Streams.importarTextPortServer());
                    serverMessages = new LinkedList<String>();
                    serverMessages.add("Server:Chat Server is running...");
                    while (true) {
                        new HandlerHostServer(serverSocket.accept(), writers).start();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
            }
        };
        worker.execute();
    }

    private void joinServer() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    clientMessages = new LinkedList<String>();
                        Thread receiverThread = new Thread(() -> {
                            try {
                                String serverMessage;
                                while ((serverMessage = clientReader.readLine()) != null) {
                                    clientMessages.add(serverMessage);
                                }
                            } catch (SocketException e) {
                                if (e.getMessage().equals("Socket closed"))
                                    //TODO no funciona
                                    clientMessages.add("Server:"+Misc.getDate()+"Exited from server.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        receiverThread.start();

                        String userInput;
                        while ((userInput = consoleReader.readLine()) != null) {
                            if ("exit".equalsIgnoreCase(userInput)) {
                                break;
                            }
                            clientWriter.println("> " + user.getUsername() + ": " + userInput);
                            clientMessages.add("> " + user.getUsername() + ": " + userInput);
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                // Puedes realizar acciones después de que el servidor haya terminado
                // Esto se ejecutará en el hilo de despacho de eventos de Swing
            }
        };
        worker.execute();
    }

    private static void setServidoresList(String email, ArrayList<Servidor> servidores) throws Exception {
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
        String documentId = null;
        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            JSONObject fields = document.getJSONObject("fields");
            if (fields.has("email")) {
                String documentEmail = fields.getJSONObject("email").getString("stringValue");
                if (documentEmail.equals(hashedEmail)) {
                    // Encontramos el documento que coincide con el correo electrónico, obtenemos su ID
                    documentId = document.getString("name").split("/")[documents.getJSONObject(0).getString("name").split("/").length - 1];
                    break;
                }
            }
        }

        if (documentId == null) {
            System.out.println("No se encontró un documento para el correo electrónico proporcionado.");
            return;
        }

        // URL de la API REST de Cloud Firestore para actualizar el documento
        String firestoreUrl = "https://firestore.googleapis.com/v1/projects/wideroom-b6ed8/databases/(default)/documents/serverslist/" + documentId;

        // Construir el JSON con la lista de servidores
        StringBuilder jsonDataBuilder = new StringBuilder();
        jsonDataBuilder.append("{\"fields\":{\"email\":{\"stringValue\":\"").append(hashedEmail).append("\"},\"servers\":{\"arrayValue\":{\"values\":[");

        for (int i = 0; i < servidores.size(); i++) {
            Servidor servidor = servidores.get(i);
            if (i > 0) {
                jsonDataBuilder.append(",");
            }
            jsonDataBuilder.append("{\"mapValue\":{\"fields\":{");
            jsonDataBuilder.append("\"name\":{\"stringValue\":\"").append(Encoding.encrypt(servidores.get(i).getName(), hashedEmail)).append("\"},");
            jsonDataBuilder.append("\"ip\":{\"stringValue\":\"").append(Encoding.encrypt(servidores.get(i).getIp(), hashedEmail)).append("\"},");
            jsonDataBuilder.append("\"textPort\":{\"stringValue\":\"").append(Encoding.encrypt(String.valueOf(servidores.get(i).getTextPort()), hashedEmail)).append("\"},");
            jsonDataBuilder.append("\"imagePortSender\":{\"stringValue\":\"").append(Encoding.encrypt(String.valueOf(servidores.get(i).getImagePortSender()), hashedEmail)).append("\"},");
            jsonDataBuilder.append("\"imagePortReceiver\":{\"stringValue\":\"").append(Encoding.encrypt(String.valueOf(servidores.get(i).getImagePortReceiver()), hashedEmail)).append("\"},");
            jsonDataBuilder.append("\"hashedPassword\":{\"stringValue\":\"").append(Encoding.encrypt(servidores.get(i).getHashedPassword(), hashedEmail)).append("\"}");
            jsonDataBuilder.append("}}}");
        }

        jsonDataBuilder.append("]}}}}");

        String jsonData = jsonDataBuilder.toString();

        // Crear la conexión HTTP para actualizar el documento
        URL updateUrl = new URL(firestoreUrl);
        HttpURLConnection updateConn = (HttpURLConnection) updateUrl.openConnection();
        updateConn.setRequestMethod("POST");
        updateConn.setRequestProperty("Content-Type", "application/json");
        updateConn.setRequestProperty("X-HTTP-Method-Override", "PATCH"); // Agregar el encabezado para emular el método PATCH
        updateConn.setDoOutput(true);

        // Escribir los datos en la conexión
        try (OutputStreamWriter writer = new OutputStreamWriter(updateConn.getOutputStream())) {
            writer.write(jsonData);
        }

        // Verificar la respuesta del servidor
        int responseCode = updateConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
            System.out.println("Error al actualizar el documento en Cloud Firestore. Código de respuesta: " + responseCode);
        } else {
            System.out.println("Documento actualizado correctamente en Cloud Firestore.");
        }
    }


    private void setButtonsEnabled(boolean modify, boolean remove, boolean join){
        if(modify){
            modifyServerButton.setBackground(Color.decode("#272727"));
            modifyServerButton.setEnabled(true);
        }else{
            modifyServerButton.setBackground(Color.BLACK);
            modifyServerButton.setEnabled(false);
        }

        if(remove){
            removeServerButton.setBackground(Color.decode("#272727"));
            removeServerButton.setEnabled(true);
        }else{
            removeServerButton.setBackground(Color.BLACK);
            removeServerButton.setEnabled(false);
        }

        if(join){
            joinServerButton.setBackground(Color.decode("#272727"));
            joinServerButton.setEnabled(true);
        }else{
            joinServerButton.setBackground(Color.BLACK);
            joinServerButton.setEnabled(false);
        }
    }
}

