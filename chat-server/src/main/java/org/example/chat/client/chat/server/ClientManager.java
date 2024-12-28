package org.example.chat.client.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {
    private final Socket socket;

    public final static ArrayList<ClientManager> clients = new ArrayList<>();

    private BufferedWriter bufferedWriter;

    private BufferedReader bufferedReader;

    private String name;

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату.");
            brodcastMessage("Server: " + name + " подключился к чату");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                ParsingMessage parsingMessage = new ParsingMessage(messageFromClient);
                if(parsingMessage.isPrivateMessage()){
                    privateMessage(parsingMessage.getMessage(), parsingMessage.getName() );
                } else {
                    brodcastMessage(messageFromClient);
                }

            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void brodcastMessage(String message){
        for (ClientManager client : clients) {
            if (!client.name.equals(name)) {
                try {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }

    private void privateMessage(String message, String userName){
        for (ClientManager client : clients) {
            if (client.name.equals(userName)) {
                try {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                    return;
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
        try {
            this.bufferedWriter.write("Server: пользователь с таким именем не найден");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат");
        brodcastMessage("Server: " + name + " покинул чат");
    }
}
