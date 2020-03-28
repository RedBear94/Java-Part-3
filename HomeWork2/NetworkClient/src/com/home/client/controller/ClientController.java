package com.home.client.controller;

import com.client.Command;
import com.home.client.view.AuthDialog;
import com.home.client.view.ClientChat;
import com.home.client.model.NetworkService;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

import static com.client.Command.*;

// Отвечает за то, что должно отображаться

public class ClientController {
    private final NetworkService networkService; // Управление Сервисом
    private final AuthDialog authDialog; // Форма аутентификации
    private final ClientChat clientChat; // Форма чата
    private String nickname;

    public ClientController(String serverHost, int serverPort) {
        this.networkService = new NetworkService(serverHost, serverPort);
        this.authDialog = new AuthDialog(this);
        this.clientChat = new ClientChat(this);
    }

    public void runApplication() throws IOException {
        connectToServer(); // Подключение к серверу
        runAuthProcess(); // Запуск процесса аутентификации
    }

    private void runAuthProcess() {
        // Создаётся анонимный класс AuthEvent() реализующий интерфейс authIsSuccessful
        /*
        networkService.setSuccessfulAuthEvent(new AuthEvent() {
            @Override
            public void authIsSuccessful(String nickname) {
                ClientController.this.setUserName(nickname);
                ClientController.this.openChat();
            }
        });*/

        networkService.setSuccessfulAuthEvent(nickname -> {
            setUserName(nickname); // При успешной аутентификации устанавливаем принятый nickname
            clientChat.setTitle(nickname);
            openChat(); // Закрывается окно для аутентификации и лткрывается окно чата
        });
        authDialog.setVisible(true);
    }

    private void openChat() {
        authDialog.dispose();
        // Передаем метод appendMessage из clientChat для
        // Consumer в качестве лямда выражения; см. аналогию в методе runAuthProcess для setSuccessfulAuthEvent
        networkService.setMessageHandler(clientChat::appendMessage);
        clientChat.setVisible(true);
    }

    private void setUserName(String nickname) {
        this.nickname = nickname;
    }

    private void connectToServer() throws IOException {
        try {
            networkService.connect(this);
        } catch (IOException e) {
            System.err.println("Failed to establish server connection");
            throw e;
        }
    }

    // Отправление логина/пароля
    public void sendAuthMessage(String login, String pass) throws IOException {
        networkService.sendCommand(authCommand(login, pass));
    }

    public void sendMessageToAllUsers(String message) {
        try {
            networkService.sendCommand(broadCastMessageCommand(message));
        } catch (IOException e) {
            clientChat.showError("Failed to send message!");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        networkService.close();
    }

    public String getUsername() {
        return nickname;
    }

    public void sendPrivateMessage(String username, String message) {
        try {
            networkService.sendCommand(privateMessageCommand(username,message));
        } catch (IOException e) {
            showErrorMessage(e.getMessage());
        }
    }

    public void showErrorMessage(String errorMessage) {
        if (clientChat.isActive()){
            clientChat.showError(errorMessage);
        } else if(authDialog.isActive()){
            authDialog.showError(errorMessage);
        }
        System.err.println(errorMessage);
    }

    public void updateUsersList(List<String> users) {
        users.remove(nickname); // удалить самого себя
        users.add(0, "All"); // добавить на 0-й индекс отправку для всех
        clientChat.updateUsers(users);
    }
}
