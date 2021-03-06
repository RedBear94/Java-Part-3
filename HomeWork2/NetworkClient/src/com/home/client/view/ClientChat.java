package com.home.client.view;

import com.home.client.controller.ClientController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ClientChat extends JFrame {

    private JPanel mainPanel;
    private JList<String> usersList;
    private JTextField messageTextField;
    private JButton sendButton;
    private JTextArea chatText;
    private JButton chsngeNickname;

    private ClientController controller;

    public static final int LINE_HISTORY_COUNT = 100;

    // Отображение данных на форму чата
    public ClientChat(ClientController controller) {
        this.controller = controller;
        // setTitle(controller.getUsername());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
        addListeners();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                controller.shutdown();
            }
        });
        chsngeNickname.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.openChangeNick();
            }
        });
    }


    private void addListeners() {
        sendButton.addActionListener(e -> ClientChat.this.sendMessage());
        messageTextField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = messageTextField.getText().trim(); // trim() - получить строку без пробелов в начале и конце
        if (message.isEmpty()) {
            return;
        }

        message = filterMessage(message); // отфильтровали сообщение

        appendOwnMessage(message);

        // Проверка кому идёт отправка сообщения и непосредственно отправка серверу
        if(usersList.getSelectedIndex() < 1) {
            controller.sendMessageToAllUsers(message);
        } else{
            String username = usersList.getSelectedValue();
            controller.sendPrivateMessage(username, message);
        }

        messageTextField.setText(null);
    }

    // Фильтрация сообщений
    private String filterMessage(String message) {
        message = controller.filterMessage(message);
        return message;
    }

    public void appendMessage(String message) {
        // invokeLater() - задание текущего Thread-а на очередь к выполнению в главный Thread
        // иными словами обработчик будет поставлен в очередь событий для AWT и будет обработан уже после очередных задач
        // реализует интерфейс Runnable, в котором есть метод run
        SwingUtilities.invokeLater(() -> {
            chatText.append(message);
            chatText.append(System.lineSeparator()); // lineSeparator() - вернет разделитель строк вашей ОС
        });
        controller.appendMessageToUserFile(message, controller.getLogin());
    }


    private void appendOwnMessage(String message) {
        appendMessage("Я: " + message);
    }


    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void updateUsers(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            DefaultListModel<String> model = new DefaultListModel<>();
            for(String person : users){
                model.addElement(person);
            }
            usersList.setModel(model);
        });
    }

    public void openLocalHistory(){
        String localHistory = controller.getLocalHistory(LINE_HISTORY_COUNT);
        chatText.append(localHistory);
    }
}