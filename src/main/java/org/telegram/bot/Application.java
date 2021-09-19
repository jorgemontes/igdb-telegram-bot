package org.telegram.bot;

import io.micronaut.runtime.Micronaut;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
