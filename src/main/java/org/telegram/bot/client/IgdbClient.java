package org.telegram.bot.client;

import com.api.igdb.request.TwitchAuthenticator;
import com.api.igdb.utils.TwitchToken;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.discovery.event.ServiceReadyEvent;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class IgdbClient implements ApplicationEventListener<ServiceReadyEvent> {

    @Value("${twitch.client.id}")
    private String clientId;

    @Value("${twitch.client.secret}")
    private String clientSecret;

    @PostConstruct
    public void initialize() {
        TwitchAuthenticator twitchAuthenticator = TwitchAuthenticator.INSTANCE;
        TwitchToken twitchToken = twitchAuthenticator.requestTwitchToken(clientId, clientSecret);
        String access_token = twitchToken.getAccess_token();
        System.out.println("token: " + access_token);
    }

    @Override
    public void onApplicationEvent(ServiceReadyEvent event) {
        initialize();
    }
}
