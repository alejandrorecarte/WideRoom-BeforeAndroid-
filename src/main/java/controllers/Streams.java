package controllers;

import models.Servidor;

import java.io.*;
import java.util.ArrayList;

public class Streams {
    public static void exportarServidores(ArrayList<Servidor> servidores) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/main/java/resources/servidores");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(servidores);
        objectWriter.close();
        fileWriter.close();
    }

    public static ArrayList<Servidor> importarServidores() throws IOException, ClassNotFoundException{
        ArrayList<Servidor> servidores;
        FileInputStream fileReader = new FileInputStream("src/main/java/resources/servidores");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        servidores = (ArrayList<Servidor>) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return servidores;
    }

    public static void exportarUsername(String username) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/main/java/resources/username");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(username);
        objectWriter.close();
        fileWriter.close();
    }

    public static String importarUsername() throws IOException, ClassNotFoundException{
        String username;
        FileInputStream fileReader = new FileInputStream("src/main/java/resources/username");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        username = (String) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return username;
    }

    public static void exportarFilesDownloadsServerPath(String path) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/main/java/resources/filesdownloadsserverpath");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(path);
        objectWriter.close();
        fileWriter.close();
    }

    public static String importarFilesDownloadsServerPath() throws IOException, ClassNotFoundException{
        String path = new String();
        FileInputStream fileReader = new FileInputStream("src/main/java/resources/filesdownloadsserverpath");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        path = (String) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return path;
    }

    public static void exportarFilesDownloadsClientPath(String path) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/main/java/resources/filesdownloadsclientpath");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(path);
        objectWriter.close();
        fileWriter.close();
    }

    public static String importarFilesDownloadsClientPath() throws IOException, ClassNotFoundException{
        String path = new String();
        FileInputStream fileReader = new FileInputStream("src/main/java/resources/filesdownloadsclientpath");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        path = (String) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return path;
    }

    public static void exportarTextPortServer(int port) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/main/java/resources/textportserver");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(port);
        objectWriter.close();
        fileWriter.close();
    }

    public static int importarTextPortServer() throws IOException, ClassNotFoundException{
        int port = 0;
        FileInputStream fileReader = new FileInputStream("src/main/java/resources/textportserver");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        port = (int) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return port;
    }

    public static void exportarImagePortSenderServer(int port) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/main/java/resources/imageportsenderserver");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(port);
        objectWriter.close();
        fileWriter.close();
    }

    public static int importarImagePortSenderServer() throws IOException, ClassNotFoundException{
        int port = 0;
        FileInputStream fileReader = new FileInputStream("src/main/java/resources/imageportsenderserver");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        port = (int) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return port;
    }

    public static void exportarImagePortReceiverServer(int port) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/main/java/resources/imageportreceiverserver");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(port);
        objectWriter.close();
        fileWriter.close();
    }

    public static int importarImagePortReceiverServer() throws IOException, ClassNotFoundException{
        int port = 0;
        FileInputStream fileReader = new FileInputStream("src/main/java/resources/imageportreceiverserver");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        port = (int) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return port;
    }

    public static void exportarAutodestroyImagesClient(boolean autodestroy) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/main/java/resources/autodestroyimagesclient");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(autodestroy);
        objectWriter.close();
        fileWriter.close();
    }

    public static boolean importarAutodestroyImagesClient() throws IOException, ClassNotFoundException{
        boolean autodestroy;
        FileInputStream fileReader = new FileInputStream("src/main/java/resources/autodestroyimagesclient");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        autodestroy = (boolean) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return autodestroy;
    }

    public static void exportarAutodestroyImagesServer(boolean autodestroy) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/main/java/resources/autodestroyimagesserver");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(autodestroy);
        objectWriter.close();
        fileWriter.close();
    }

    public static boolean importarAutodestroyImagesServer() throws IOException, ClassNotFoundException{
        boolean autodestroy;
        FileInputStream fileReader = new FileInputStream("src/main/java/resources/autodestroyimagesserver");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        autodestroy = (boolean) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return autodestroy;
    }
}