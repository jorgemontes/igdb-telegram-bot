package org.telegram.bot.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.telegram.bot.IgdbWebhookBot;

import javax.inject.Inject;

@Controller("/igdb")
public class BotController {

    @Inject
    private IgdbWebhookBot igdbWebhookBot;

    @Get("/")
    public String getInfo(){
        return "";
    }
}
