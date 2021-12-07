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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.inject.Inject;

@Controller("/igdb")
public class BotController {

    @Inject
    private IgdbWebhookBot igdbWebhookBot;

    @Inject
    private IgdbClient igdbClient;

    @Post("/callback/")
    public InlineQueryResult getInfo(Update update) {
        InlineQueryResultArticle hola = InlineQueryResultArticle.builder().inputMessageContent(InputTextMessageContent.builder().messageText("q ase").build()).title("title").id(update.getUpdateId()+"").build();
        try {
            igdbClient.jsonQuery();
        } catch (RequestException | InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return hola;
    }
}
