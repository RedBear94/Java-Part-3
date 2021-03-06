package com.company.client;

import com.client.Command;
import com.client.CommandType;
import com.client.command.AuthCommand;
import com.client.command.BroadcastMessageCommand;
import com.client.command.PrivateMessageCommand;
import com.client.command.UpdateUserNicknameCommand;
import com.company.NetworkServer;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private final ExecutorService executor;

    private final NetworkServer networkServer;
    private final Socket clientSocket;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String nickname;

    long start = System.currentTimeMillis();
    Thread timeOutThread = new Thread(() ->{
        startTimeOut(start, 10);
    });

    public ClientHandler(NetworkServer networkServer, Socket socket) {
        this.networkServer = networkServer;
        this.clientSocket = socket;
        executor = Executors.newCachedThreadPool();
        // doHandle(socket);
    }

    private void doHandle(Socket socket) {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // HomeWork4
            /*
            // Вар 1
            executor.submit(new Runnable() {
                public void run() {
                    try {
                        authentication();
                        readMessages();
                    } catch (IOException e){
                        System.out.println("Соеденинение с клиентом " + nickname + " было закрыто!");
                    } finally {
                        timeOutThread.interrupt();
                        closeConnection();
                    }
                }
            });*/

            // Вар 2
            executor.execute(
                    new Thread(() ->{
                        try {
                            authentication();
                            readMessages();
                        } catch (IOException e){
                            System.out.println("Соеденинение с клиентом " + nickname + " было закрыто!");
                        } finally {
                            timeOutThread.interrupt();
                            closeConnection();
                        }
                    })
            );

            // Old Ver
            /*new Thread(() ->{
                try {
                    authentication();
                    readMessages();
                } catch (IOException e){
                    System.out.println("Соеденинение с клиентом " + nickname + " было закрыто!");
                } finally {
                    timeOutThread.interrupt();
                    closeConnection();
                }
            }).start();*/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            networkServer.unsubscribe(this);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() throws IOException {
        while (true){
            Command command = readCommand();
            if (command == null) {
                continue;
            }
            switch (command.getType()){
                case END:
                    System.out.println("Recieved 'END' coommand");
                    return;
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommand commandData = (PrivateMessageCommand) command.getData();
                    String receiver = commandData.getReceiver();
                    String message = commandData.getMessage();
                    networkServer.personalMessage(receiver, Command.messageCommand(nickname, message));
                    break;
                }
                case BROADCAST_MESSAGE: {
                    BroadcastMessageCommand commandData = (BroadcastMessageCommand) command.getData();
                    String message = commandData.getMessage();
                    networkServer.broadcastMessage(Command.messageCommand(nickname, message), this);
                    break;
                }
                case UPDATE_USER_NICKNAME:
                    UpdateUserNicknameCommand commandData = (UpdateUserNicknameCommand) command.getData();
                    String oldNickname = commandData.getOldNickname();
                    String newNickName = commandData.getNewNickname();
                    networkServer.updateUsersNicknames(oldNickname, newNickName);
                    break;
                default:
                    System.err.println("Unknown type of command : " + command.getType());
                    break;
            }
        }
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) in.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown type of object from client!";
            e.printStackTrace();
            sendMessage(Command.errorCommand(errorMessage));
            return null;
        }
    }

    private void authentication() throws IOException {
        timeOutThread.start();

        while (true){
            Command command = readCommand();
            if (command == null) {
                continue;
            }

            if (command.getType() == CommandType.AUTH) {
                boolean successfulAuth = processAuthCommand(command);
                if(successfulAuth){
                    timeOutThread.interrupt();
                    return;
                }
            } else {
                System.err.println("Unknown type of command for auth process: " + command.getType());
            }
        }
    }

    private void startTimeOut(long start, int whenStop) {
        while(!Thread.currentThread().isInterrupted()){
            long timeSpent = (System.currentTimeMillis() - start) / 1000;
            if(timeSpent > whenStop){
                Command command = Command.endCommand();
                try {
                    sendMessage(command);
                    closeConnection();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean processAuthCommand(Command command) throws IOException {
        AuthCommand commandData = (AuthCommand) command.getData();
        String login = commandData.getLogin();
        String password = commandData.getPassword();
        String username = networkServer.getAuthService().getUsernameByLoginAndPassword(login, password);
        if (username == null) {
            Command authErrorCommand = Command.authErrorCommand("Отсутвует учетная запись по данному логину и паролю");
            sendMessage(authErrorCommand);
            return false;
        } else if(networkServer.isNicknameBusy(username)){
            Command authErrorCommand = Command.authErrorCommand("Данный пользователь уже авторизован!");
            sendMessage(authErrorCommand);
            return false;
        }
        else {
            nickname = username;
            String message = nickname + " защел в чат!";
            networkServer.broadcastMessage(Command.messageCommand(null, message), this);
            commandData.setUsername(nickname);
            sendMessage(command);
            networkServer.subscribe(this);
            return true;
        }
    }

    public void sendMessage(Command command) throws IOException{
        out.writeObject(command);
    }

    public void run() {
        doHandle(clientSocket);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) { this.nickname = nickname; }
}
