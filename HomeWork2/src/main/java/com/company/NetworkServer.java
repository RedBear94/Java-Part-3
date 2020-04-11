package com.company;

import com.client.Command;
import com.company.auth.AuthService;
import com.company.auth.DatabaseAuthService;
import com.company.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkServer {
    private final int port;
    private static ServerLogger logic = new ServerLogger();
    // private final List<ClientHandler> clients = new ArrayList<>();

    // только итератор требует синхонизации
    // private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    // CopyOnWriteArrayList - т.к. работа идёт с копией массива, что гарантирует синхронизированную работу с данными
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final DatabaseAuthService authService;

    public NetworkServer(int port) {
        this.port = port;
        this.authService =  new DatabaseAuthService();
    }

    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер был успешно запущен на порту " + port);
            logic.serverRun(port);

            authService.start();
            while (true){
                System.out.println("Ожидание клиентского подключения...");
                logic.waitConnection();

                Socket clientSocket = serverSocket.accept();

                System.out.println("Клиент подключился");
                logic.clientConnected();
                // ClientHandler - Логика подключения клиентов с которыми работаем
                // clients.add(new ClientHandler(this, clientSocket));
                createClientHandler(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при работе сервера");
            logic.serverError();
            e.printStackTrace();
        } finally {
            authService.stop();
        }
    }

    private void createClientHandler(Socket clientSocket) {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.run();
    }

    public AuthService getAuthService(){
        return authService;
    }

    // synchronized - гарантирует что список клиентов (используемые данные) в момент работы фукций не будет изменен
    public /*synchronized*/ void personalMessage(String nickname, Command commandMessage) throws IOException{
        boolean flag = false;
        for(ClientHandler client : clients){
            if (nickname.equals(client.getNickname())){
                client.sendMessage(commandMessage);
                flag = true;
            }
        }
        if(flag == false){
            System.out.println("Пользователь " + nickname + " не найден");
            logic.cantFindUser(nickname);
        }
    }

    public /*synchronized*/ void broadcastMessage(Command message, ClientHandler owner) throws IOException{
        for(ClientHandler client : clients){
            if(client != owner){
                client.sendMessage(message);
            }
        }
    }

    public /*synchronized*/ void unsubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        List<String> users = getAllUserNames();
        broadcastMessage(Command.updateUserListCommand(users), null);
    }

    public /*synchronized*/ void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        List<String> users = getAllUserNames();
        broadcastMessage(Command.updateUserListCommand(users), null);
    }

    private List<String> getAllUserNames() {
        /* Вар 2
        return clients.stream()
                .map(client -> client.getNickname())
                .collect(Collectors.toList());
        */

        List<String> usernames = new LinkedList<>();
        for(ClientHandler clientHandler : clients){
            usernames.add(clientHandler.getNickname());
        }
        return usernames;
    }

    public boolean isNicknameBusy(String username) {
        for(ClientHandler client : clients){
            if(client.getNickname().equals(username)){
                return true;
            }
        }
        return false;
    }


    public void updateUsersNicknames(String oldUsername, String newUsername) throws IOException {
        for(ClientHandler client : clients){
            if(client.getNickname().equals(oldUsername)){
                client.setNickname(newUsername);
                try {
                    authService.UpdateDB(oldUsername, newUsername);
                    List<String> users = getAllUserNames();
                    broadcastMessage(Command.updateUserListCommand(users), null);
                    logic.userNameUpdated(oldUsername, newUsername);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    client.sendMessage(Command.errorCommand("Ошибка обновления имени пользователя, имя занято"));
                    logic.errorUpdateUser();
                    client.setNickname(oldUsername);
                    e.printStackTrace();
                }
            }
        }
    }
}
