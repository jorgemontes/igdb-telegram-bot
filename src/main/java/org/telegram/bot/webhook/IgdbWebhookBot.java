package org.telegram.bot.webhook;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.discovery.event.ServiceReadyEvent;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class IgdbWebhookBot extends TelegramWebhookBot implements ApplicationEventListener<ServiceReadyEvent> {

    public static final String IGDB_CALLBACK = "/igdb/";

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
            URI uri = URI.create(callbackurl + IGDB_CALLBACK);
            SetWebhook setWebhook = new SetWebhook().builder().url(uri.normalize().toString()).build();
            setWebhook.validate();
            setWebhook(setWebhook);
            botsApi.registerBot(this, setWebhook);
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
        System.out.println("Update received" + update);
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }
}
