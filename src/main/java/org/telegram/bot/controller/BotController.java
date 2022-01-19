package org.telegram.bot.controller;

import com.api.igdb.exceptions.RequestException;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.internal.text.StringEscapeUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.apache.commons.lang3.StringUtils;
import org.telegram.bot.client.IgdbClient;
import org.telegram.bot.webhook.IgdbWebhookBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import proto.Game;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/igdb")
public class BotController {

    private static final String[] SEARCH_LIST = {".", "-", "#", "(", ")", "+", "!"};

    @Inject
    private IgdbWebhookBot igdbWebhookBot;

    @Inject
    private IgdbClient igdbClient;

    @Post("/callback/")
    public void getInfo(Update update) throws InvalidProtocolBufferException, RequestException, TelegramApiException {
        String scapedQuery = StringEscapeUtils.escapeJava(update.getInlineQuery().getQuery());
        List<Game> games = igdbClient.jsonQuery(scapedQuery);
        List<InlineQueryResult> answers = mapToAnswerInlineQuery(games);
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(update.getInlineQuery().getId());
        answerInlineQuery.setResults(answers);
        answerInlineQuery.validate();
        igdbWebhookBot.execute(answerInlineQuery);
    }

    private List<InlineQueryResult> mapToAnswerInlineQuery(List<Game> games) {
        String[] replacementParams = Arrays.stream(SEARCH_LIST)
                .map(s -> "\\" + s)
                .toList()
                .toArray(String[]::new);
        return games.stream().map(game ->
                InlineQueryResultArticle.builder().id(game.getId() + "")
                        .title(getTitleWithYear(game))
                        .description(game.getSummary())
                        .thumbUrl(getThumbUrl(game))
                        .inputMessageContent(getMessageContent(game, replacementParams))
                        .build()
        ).collect(Collectors.toList());
    }

    private String getTitleWithYear(Game game) {
        int year = 0;
        if (game.hasFirstReleaseDate()) {
            year = LocalDateTime.ofEpochSecond(game.getFirstReleaseDate().getSeconds(), 0, ZoneOffset.UTC).get(ChronoField.YEAR);
        }
        return game.getName() + " (" + year + ")";
    }

    private InputTextMessageContent getMessageContent(Game game, String[] replacementList) {
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("numberFormat", StringHelpers.numberFormat);
        Template template = null;
        String markdownContent = null;
        try {
            template = handlebars.compile("template");
            markdownContent = template.apply(game);
            markdownContent = StringUtils.replaceEach(markdownContent, SEARCH_LIST, replacementList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return InputTextMessageContent.builder().messageText(markdownContent).parseMode(ParseMode.MARKDOWNV2).build();
    }

    private String getThumbUrl(Game game) {
        if (game.getArtworksCount() > 0)
            return "https:" + game.getArtworks(0).getUrl();
        else
            return "";
    }
}
