package org.telegram.bot;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventListener;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import io.micronaut.discovery.event.ServiceReadyEvent;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class IgdbWebhookBot extends TelegramWebhookBot implements ApplicationEventListener<ServiceReadyEvent> {

    @Value("${org.telegram.bot.username}")
    private String username;

    @Value("${org.telegram.bot.token}")
    private String botToken;

    @Value("${org.telegram.bot.callback.url}")
    private String callbackurl;

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
            SetWebhook setWebhook = SetWebhook.builder().url(callbackurl+"/igdb/message").build();
            botsApi.registerBot(this,setWebhook);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApplicationEvent(ServiceReadyEvent event) {
        register();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }
}
