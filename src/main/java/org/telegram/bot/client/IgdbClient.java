package org.telegram.bot.client;

import com.api.igdb.request.TwitchAuthenticator;
import com.api.igdb.utils.TwitchToken;
import io.micronaut.context.annotation.Value;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class IgdbClient {

    @Value("${twitch.client.id}")
    private String clientId;

    @Value("${twitch.client.secret}")
    private String clientSecret;

    @PostConstruct
    public void initialize(){

        var twitchAuthenticator = TwitchAuthenticator.INSTANCE;
        var twitchToken = twitchAuthenticator.requestTwitchToken(clientId, clientSecret);
        String access_token = twitchToken.getAccess_token();
        System.out.println("token: "+access_token);

    }
}
