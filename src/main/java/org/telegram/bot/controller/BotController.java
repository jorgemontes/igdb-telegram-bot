package org.telegram.bot.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.telegram.bot.IgdbWebhookBot;

import javax.inject.Inject;

@Controller("/igdb")
public class BotController {

    @Inject
    private IgdbWebhookBot igdbWebhookBot;

    @Post("/")
    public String getInfo(){
        return "";
    }
}
