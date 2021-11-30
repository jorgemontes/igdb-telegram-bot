package org.telegram.bot.controller;

import com.api.igdb.exceptions.RequestException;
import com.google.protobuf.InvalidProtocolBufferException;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.telegram.bot.IgdbWebhookBot;
import org.telegram.bot.client.IgdbClient;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.inject.Inject;

@Controller("/igdb")
public class BotController {

    @Inject
    private IgdbWebhookBot igdbWebhookBot;

    @Inject
    private IgdbClient igdbClient;

    @Post("/callback/")
    public Message getInfo(Message message) {
        System.out.println(message);
        try {
            igdbClient.jsonQuery();
            igdbWebhookBot.execute(SendMessage.builder().chatId(message.getChatId().toString()).text(message.getText()).build());
        } catch (TelegramApiException | RequestException | InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return message;
    }
}
