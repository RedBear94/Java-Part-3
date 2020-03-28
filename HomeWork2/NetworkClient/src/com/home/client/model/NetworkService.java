package com.home.client.model;

import com.client.Command;
import com.client.command.AuthCommand;
import com.client.command.ErrorCommand;
import com.client.command.MessageCommand;
import com.client.command.UpdateUsersListCommand;
import com.home.client.controller.AuthEvent;
import com.home.client.controller.ClientController;
import com.sun.security.ntlm.Client;

import javax.smartcardio.CommandAPDU;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

// Отвечает за логику
public class NetworkService {
    private final String host;
    private final int port;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ClientController controller;

    // Обработчик сообщений
    private Consumer<String> messageHandler; // Consumer - втроенный интерфейс,
    // может выполнить действие над объектом указанного типа (например String),
    // через accept() - по умолчанию ничего не выполняет
    private AuthEvent successfulAuthEvent;
    private String nickname; // username пользователя в BaseAuthService

    public NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Выполнить соеденинение с сервером
    public void connect(ClientController controller) throws IOException {
        this.controller = controller;
        socket = new Socket(host, port);
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        runReadThread();
    }

    // Поток на чтение, получение сообщений от сервера
    private void runReadThread() {
        new Thread(() -> {
            while (true) {
                try {
                    Command command = (Command) in.readObject();
                    switch (command.getType()){
                        case AUTH: {
                            AuthCommand commandData = (AuthCommand) command.getData();
                            nickname = commandData.getUsername();
                            // Вызватся событие интерфейса при успешной аутентификации
                            // authIsSuccessful() - описана в runAuthProcess() из ClientController
                            successfulAuthEvent.authIsSuccessful(nickname);
                            break;
                        }
                        case MESSAGE: {
                            MessageCommand commandData = (MessageCommand) command.getData();
                            if(messageHandler != null){
                                String message = commandData.getMessage();
                                String username = commandData.getUsername();
                                if(username != null){
                                    message = username + " :" + message;
                                }
                                // accept - пронициализирован в ClientControl в ф-иие openChat()
                                // через setMessageHandler
                                messageHandler.accept(message);
                            }
                            break;
                        }
                        case AUTH_ERROR:
                        case ERROR: {
                            ErrorCommand commandData = (ErrorCommand) command.getData();
                            controller.showErrorMessage(commandData.getErrorMessage());
                            break;
                        }
                        case UPDATE_USERS_LIST: {
                            UpdateUsersListCommand commandData = (UpdateUsersListCommand) command.getData();
                            List<String> users = commandData.getUsers();
                            controller.updateUsersList(users);
                            break;
                        }
                        case END: {
                            controller.shutdown();
                            System.exit(0);
                            break;
                        }
                        default:
                            System.err.println("Unknown type of command: " + command.getType());
                            break;
                    }
                } catch (IOException e) {
                    System.out.println("Поток чтения был прерван!");
                    return;
                } catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Отпрвить сообщение серверу
    public void sendCommand(Command command) throws IOException {
        out.writeObject(command);
    }

    // Задать обработчик сообщений
    // setMessageHandler - принимает лямда выражение для Consumer типа String
    public void setMessageHandler(Consumer<String> messageHandler) {
        // в messageHandler - передали фукцию, которая принимающую переменную типа String
        // @Override .accept()
        this.messageHandler = messageHandler;

        // Consumer<Integer> messageHandler = message -> System.out.printf("%d долларов \n", message);
        // messageHandler.accept(100); // 100 долларов
    }

    // Выполниться в случае успешной аутентификации
    public void setSuccessfulAuthEvent(AuthEvent successfulAuthEvent) {
        this.successfulAuthEvent = successfulAuthEvent; // Принимает лямда выражение
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
