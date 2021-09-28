package org.telegram.bot;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventListener;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import io.micronaut.discovery.event.ServiceReadyEvent;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class IgdbWebhookBot extends TelegramLongPollingBot implements ApplicationEventListener<ServiceReadyEvent> {

    @Value("${org.telegram.bot.username}")
    private String username;

    @Value("${org.telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    @PostConstruct
    public void register() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        //System.out.println(update.getMessage().getText());
        ArrayList<Update> updateArrayList = null;
        try {
            updateArrayList = execute(new GetUpdates());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        updateArrayList.stream().map(Update::getMessage).map(Message::getText).peek(System.out::println);


    }

    @Override
    public void onApplicationEvent(ServiceReadyEvent event) {
        register();
    }
}
