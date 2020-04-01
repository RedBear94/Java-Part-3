package com.home.client.controller;

import com.client.Command;
import com.home.client.view.AuthDialog;
import com.home.client.view.ChangeNickname;
import com.home.client.view.ClientChat;
import com.home.client.model.NetworkService;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.client.Command.*;

// Отвечает за то, что должно отображаться

public class ClientController {
    private final NetworkService networkService; // Управление Сервисом
    private final AuthDialog authDialog; // Форма аутентификации
    private final ClientChat clientChat; // Форма чата
    private final ChangeNickname changeNickname;
    private String nickname;
    private String login;
    private String oldNickname;

    private final List<String> badWordsList = new ArrayList<String>();

    public ClientController(String serverHost, int serverPort) {
        this.networkService = new NetworkService(serverHost, serverPort);
        this.authDialog = new AuthDialog(this);
        this.clientChat = new ClientChat(this);
        this.changeNickname = new ChangeNickname(this);
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
            loadBadWord();  // Загрузка списка слов для цензуры
            openChat(); // Закрывается окно для аутентификации и лткрывается окно чата
        });
        authDialog.setVisible(true);
    }

    // Загрузка списка цензурируемых слов
    private void loadBadWord() {
        String badWord;
        try {
            Path path = Paths.get("badWord.txt");
            BufferedReader in = Files.newBufferedReader(path);
            while((badWord = in.readLine()) != null){
                badWordsList.add(badWord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openChat() {
        authDialog.dispose();
        // Передаем метод appendMessage из clientChat для
        // Consumer в качестве лямда выражения; см. аналогию в методе runAuthProcess для setSuccessfulAuthEvent
        networkService.setMessageHandler(clientChat::appendMessage);
        clientChat.openLocalHistory();
        clientChat.setVisible(true);
    }

    private void setUserName(String nickname) {
        this.nickname = nickname;
    }

    public void setOldNickname(String oldNickname) {
        this.oldNickname = oldNickname;
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
        this.login = login;
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
        } else if(changeNickname.isActive()){
            changeNickname.showError(errorMessage);
            setUserName(oldNickname);
            clientChat.setTitle(oldNickname);
        }
        System.err.println(errorMessage);
    }

    public void updateUsersList(List<String> users) {
        users.remove(nickname); // удалить самого себя
        users.add(0, "All"); // добавить на 0-й индекс отправку для всех
        clientChat.updateUsers(users);
    }

    public void openChangeNick(){
        setOldNickname(nickname);
        changeNickname.setVisible(true);
    }

    public void updateNickname(String nickname) {
        try {
            networkService.sendCommand(updateUserNickname(this.nickname, nickname));
            setUserName(nickname);
            clientChat.setTitle(nickname);
        } catch (IOException e) {
            showErrorMessage(e.getMessage());
        }
    }

    public void appendMessageToUserFile(String message, String login) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream("history_" + login + ".txt", true))) {
            byte[] buffer = message.getBytes();
            out.write(buffer);
            out.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLogin() {
        return login;
    }

    public String getLocalHistory(int lineCount) {
        String login = getLogin();
        String history = "";
        String historyLine;

        List<String> historyList = new ArrayList<String>();
        try {
            Path path = Paths.get("history_" + login + ".txt");
            BufferedReader in = Files.newBufferedReader(path);
            while((historyLine = in.readLine()) != null){
                historyList.add(historyLine);
            }

            int readCount = historyList.size() - lineCount;
            if(readCount < 0)
                readCount = 0;

            for(int i = readCount; i < historyList.size(); i++) {
                history += historyList.get(i) + System.lineSeparator();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return history;
    }

    // Фильтрация сообщений
    public String filterMessage(String message) {
        int index;
        int size;
        for(int i = 0; i < badWordsList.size(); i++) {
            index = message.toUpperCase().indexOf(badWordsList.get(i).toUpperCase());
            size = badWordsList.get(i).length();
            // System.out.println("index = " + index);
            // System.out.println("size = " + size);
            if(index >= 0){
                message = message.substring(0,index) + "[цензура]" + message.substring(index + size);
            }
            // message = message.replace(badWordsList.get(i), "[цензура]"); // с учетом регистра
        }
        return message;
    }
}
