package org.telegram.bot.controller;

import com.api.igdb.exceptions.RequestException;
import com.google.common.collect.Lists;
import com.google.protobuf.InvalidProtocolBufferException;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.telegram.bot.IgdbWebhookBot;
import org.telegram.bot.client.IgdbClient;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

import javax.inject.Inject;
import java.util.List;

@Controller("/igdb")
public class BotController {

    @Inject
    private IgdbWebhookBot igdbWebhookBot;

    @Inject
    private IgdbClient igdbClient;

    @Post("/callback/")
    public List<InlineQueryResult> getInfo(Update update) throws TelegramApiValidationException {
        InlineQueryResultArticle hola = InlineQueryResultArticle.builder().inputMessageContent(InputTextMessageContent.builder().messageText("q ase").build()).title("title").id(update.getUpdateId()+"").build();
        try {
            igdbClient.jsonQuery();
        } catch (RequestException | InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(update.getInlineQuery().getId());
        answerInlineQuery.setResults(Lists.newArrayList(hola));
        answerInlineQuery.validate();

        return Lists.newArrayList(hola);
    }
}
