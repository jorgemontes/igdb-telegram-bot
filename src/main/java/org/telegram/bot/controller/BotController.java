package org.telegram.bot.controller;

import com.api.igdb.exceptions.RequestException;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.internal.text.StringEscapeUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.telegram.bot.IgdbWebhookBot;
import org.telegram.bot.client.IgdbClient;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import proto.Game;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/igdb")
public class BotController {

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
        return games.stream().map(game ->
                InlineQueryResultArticle.builder().id(game.getId() + "")
                        .title(getTitleWithYear(game))
                        .description(game.getSummary())
                        .thumbUrl(getThumbUrl(game))
                        .inputMessageContent(getMessageContent(game))
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

    private InputTextMessageContent getMessageContent(Game game) {
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("numberFormat", StringHelpers.numberFormat);
        Template template = null;
        String markdownContent = null;
        try {
            template = handlebars.compile("template");
            markdownContent = template.apply(game);
            markdownContent = markdownContent.replaceAll("\\.", "\\\\.").replaceAll("\\-", "\\\\-").replaceAll("\\#", "\\\\#").replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("\\+", "\\\\+").replaceAll("\\!", "\\\\!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(markdownContent);
        return InputTextMessageContent.builder().messageText(markdownContent).parseMode(ParseMode.MARKDOWNV2).build();
    }

    private String getThumbUrl(Game game) {
        if (game.getArtworksCount() > 0)
            return "https:" + game.getArtworks(0).getUrl();
        else
            return "";
    }
}
