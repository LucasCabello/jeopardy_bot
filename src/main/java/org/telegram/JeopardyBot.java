package org.telegram;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;

public class JeopardyBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println("Mensaje entrante: " + update.getMessage().getText());
            try {
                SendMessage message = new SendMessage();
                if(update.getMessage().getText().equals("/start")) {
                    message = new SendMessage()
                            .setChatId(update.getMessage().getChatId())
                            .setText("Introduce un concepto para generar una respuesta de Jeopardy.");
                }
                else {
                    message = new SendMessage()
                            .setChatId(update.getMessage().getChatId())
                            .setText(SearchWikiIndex.startSearchApp(update.getMessage().getText()));
                }
                sendMessage(message);
                System.out.println("Mensaje saliente: " + message.getText());
            }
            catch (TelegramApiException | IOException  e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "ducks_bot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("DUCKS_TOKEN");
    }
}