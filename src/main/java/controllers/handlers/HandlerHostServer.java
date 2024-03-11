package controllers.handlers;

import controllers.Misc;
import controllers.Streams;
import controllers.frameControllers.MainFrame;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static controllers.Encoding.*;
import static controllers.frameControllers.HostServerFrame.messages;

public class HandlerHostServer extends Thread {
    private final Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private static ArrayList<PrintWriter> writers;
    private static ArrayList<String> connectedUsers = new ArrayList<String>();
    private static ArrayList<String> connectedIPs = new ArrayList<String>();
    public HandlerHostServer(Socket socket ,ArrayList<PrintWriter> writers) {
        this.socket = socket;
        this.writers = writers;
    }

    @Override

    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writers.add(writer);

            while (true) {
                String message = reader.readLine();
                if (message == null) {
                    return;
                }
                boolean isCommand = false;
                try {
                    if (message.split("/")[1].equals("requestHashedPassword")) {
                        writer.println("HashedPassword: " + MainFrame.hostHashedPassword);
                        isCommand = true;
                    }
                }catch(ArrayIndexOutOfBoundsException e){}
                if(!isCommand) {
                    message = decrypt(message, MainFrame.hostHashedPassword);
                    MainFrame.serverMessages.add("(" + socket.getInetAddress() + "):" + message);
                    isCommand = detectarComandos(message, writer);
                    detectarMensajesUsuario(message);
                    if(!isCommand){
                        broadcast(message, writer);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (writer != null) {
                writers.remove(writer);
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void broadcast(String message, PrintWriter messageWriter) {
        for (PrintWriter writer : writers) {
            if(!writer.equals(messageWriter)){
                writer.println(encrypt(message, MainFrame.hostHashedPassword));
            }
        }
    }

    public static void broadcastServerMessage(String message){
        for (PrintWriter writer : writers) {
            writer.println(encrypt("Server:" + Misc.getDate()+ ":" +message, MainFrame.hostHashedPassword));
        }
    }

    static class ImageConnectionHandler implements Runnable {
        private Socket socket;
        private String sender;

        public ImageConnectionHandler(Socket socket, String sender) {
            this.socket = socket;
            this.sender = sender;
        }

        @Override
        public void run() {
            try {
                Calendar calendar = Calendar.getInstance();
                InputStream inputStream = socket.getInputStream();
                String fileName = Streams.importarFilesDownloadsServerPath() + "/image" + sender + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.MONTH)
                        + calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) + ".jpg";
                FileOutputStream fileOutputStream = new FileOutputStream(fileName);

                byte[] receiveBuffer = new byte[1024];
                int receiveBytesRead;

                while ((receiveBytesRead = inputStream.read(receiveBuffer)) != -1) {
                    fileOutputStream.write(receiveBuffer, 0, receiveBytesRead);
                }
                socket.close();
                Thread.sleep(100);
                for(int i = 0; i < connectedIPs.size(); i++) {
                    try (Socket imageSocket = new Socket(connectedIPs.get(i), Streams.importarImagePortSenderServer());
                         OutputStream outputStream = imageSocket.getOutputStream();
                         FileInputStream fileInputStream = new FileInputStream(fileName)) {
                        Thread.sleep(100);

                        byte[] sendBuffer = new byte[1024];
                        int sendBytesRead;

                        while ((sendBytesRead = fileInputStream.read(sendBuffer)) != -1) {
                            outputStream.write(sendBuffer, 0, sendBytesRead);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static boolean detectarComandos(String message, PrintWriter writer){
        try {
            if (message.split("/")[3].equals("list")) {
                String users = "Server:"+ Misc.getDate()+":Connected users (" + connectedUsers.size() + " users) > ";
                for (int i = 0; i < connectedUsers.size(); i++) {
                    if(i != connectedUsers.size()-1) {
                        users += connectedUsers.get(i) + ", ";
                    }else{
                        users += connectedUsers.get(i);
                    }
                }
                writer.println(encrypt(users, MainFrame.hostHashedPassword));
                return true;
            }else if(message.split("/")[3].split(" ")[0].equals("kick")) {
                for(int i = 0; i < connectedUsers.size(); i++) {
                    boolean encontrado = false;
                    String user = "";
                    try {
                        user = message.split("/")[3].split(" ")[1];
                    }catch(IndexOutOfBoundsException e){}
                    if(connectedUsers.get(i).equals(user)) {
                        writers.get(i).println(encrypt("Server:" + Misc.getDate() + ":You have been kicked from the server.", MainFrame.hostHashedPassword));
                        writers.get(i).close();
                        writers.remove(i);
                        connectedIPs.remove(i);
                        connectedUsers.remove(i);
                        encontrado = true;
                        broadcastServerMessage(user + " has been kicked");
                    }
                    if(!encontrado){
                        writers.get(i).println(encrypt("Server:" + Misc.getDate() + ":" + user + " not found.", MainFrame.hostHashedPassword));
                    }
                }
                return true;
            }
        }catch(ArrayIndexOutOfBoundsException e){}
        return false;
    }

    public static void detectarMensajesUsuario(String message){
        try {
            if (message.split(" ")[2].equals("joined")) {
                connectedUsers.add(message.split(":")[3].split(" ")[0]);
                connectedIPs.add(MainFrame.serverMessages.getLast().split("/")[1].split(":")[0].replace(")", ""));
            }if (message.split(" ")[2].equals("left")) {
                connectedUsers.remove(message.split(":")[3]);
                int slashIndex = message.indexOf('/');

                if (slashIndex != -1) {
                    // Encuentra la posición del primer ')'
                    int closingParenthesisIndex = message.indexOf(')', slashIndex);

                    if (closingParenthesisIndex != -1) {
                        // Extrae la subcadena entre '/' y ')'
                        String ipAddress = message.substring(slashIndex + 1, closingParenthesisIndex);
                        connectedIPs.remove(ipAddress);
                    }
                }
            }
            if (message.split(" ")[3].equals("sent")) {
                try (ServerSocket imageSocketServer = new ServerSocket(Streams.importarImagePortReceiverServer())){;
                    Socket imageSocket = imageSocketServer.accept();
                    Thread handlerThread = new Thread(new ImageConnectionHandler(imageSocket, message.split(":")[0]));
                    handlerThread.start();
                }catch(SocketException e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){}
    }
}