package com.company;

import com.client.CommandType;
import org.apache.log4j.Logger;

public class ServerLogger {
    private static final Logger log = Logger.getLogger(ServerLogger.class);

    public void wrongPort(){
        log.error("Некоректный формат порта, будет использоватьбся порт по умолчанию");
    }

    public void serverRun(int port){
        log.info("Сервер был успешно запущен на порту " + port);
    }

    public void waitConnection(){
        log.info("Ожидание клиентского подключения...");
    }

    public void clientConnected(){
        log.info("Клиент подключился");
    }

    public void serverError(){
        log.error("Ошибка при работе сервера");
    }

    public void cantFindUser(String nickname){
        log.info("Пользователь " + nickname + " не найден");
    }

    public void errorUpdateUser(){
        log.error("Ошибка обновления имени пользователя, имя занято");
    }

    public void connectionClosed(String nickname) {
        log.info("Соеденинение с клиентом " + nickname + " было закрыто!");
    }

    public void receivedUnknownCommand(CommandType type) {
        log.warn("Unknown type of command : " + type);
    }

    public void receivedUnknownObjectFromClient() {
        log.warn("Unknown type of object from client!");
    }

    public void unknownCommandForAuthProcess(CommandType type) {
        log.error("Unknown type of command for auth process: " + type);
    }

    public void wrongLoginPassword() {
        log.error("Отсутвует учетная запись по данному логину и паролю");
    }

    public void alreadyAuth() {
        log.error("Данный пользователь уже авторизован!");
    }

    public void clientOpenChat(String nickname) {
        log.info(nickname + " защел в чат!");
    }

    public void clientSendMessage(String username){
        log.info("Клиент " + username + " прислал сообщение");
    }


    public void tryUpdateUserName(String oldNickname, String newNickName) {
        log.info("Клиент " + oldNickname + " пытается изменить имя на " + newNickName);
    }

    public void userNameUpdated(String oldNickname, String newNickName){
        log.info("Клиент " + oldNickname + " успешно изменил своё имя на " + newNickName);
    }
}
