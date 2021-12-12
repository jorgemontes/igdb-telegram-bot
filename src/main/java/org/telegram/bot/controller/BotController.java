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
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;
import proto.Game;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/igdb")
public class BotController {

    @Inject
    private IgdbWebhookBot igdbWebhookBot;

    @Inject
    private IgdbClient igdbClient;

    @Post("/callback/")
    public List<InlineQueryResult> getInfo(Update update) throws InvalidProtocolBufferException, RequestException, TelegramApiException {
        List<Game> games = igdbClient.jsonQuery(update.getInlineQuery().getQuery());
        List<InlineQueryResult> answers = mapToAnswerInlineQuery(games);
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(update.getInlineQuery().getId());
        answerInlineQuery.setResults(answers);
        answerInlineQuery.validate();
        igdbWebhookBot.execute(answerInlineQuery);
        return Lists.newArrayList();
    }

    private List<InlineQueryResult> mapToAnswerInlineQuery(List<Game> games) {
        return games.stream().map(game -> {
            InlineQueryResultArticle inlineQueryResultArticle = new InlineQueryResultArticle();
            inlineQueryResultArticle.setId(game.getId() + "");
            inlineQueryResultArticle.setTitle(game.getName());
            inlineQueryResultArticle.setDescription(game.getSummary());
            inlineQueryResultArticle.setInputMessageContent( new InputTextMessageContent("lol"));
            return inlineQueryResultArticle;
        }).collect(Collectors.toList());
    }
}
