package org.telegram.bot.client;

import com.api.igdb.exceptions.RequestException;
import com.api.igdb.request.IGDBWrapper;
import com.api.igdb.request.TwitchAuthenticator;
import com.api.igdb.utils.Endpoints;
import com.api.igdb.utils.TwitchToken;
import com.google.protobuf.InvalidProtocolBufferException;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.discovery.event.ServiceReadyEvent;
import proto.Game;
import proto.GameResult;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class IgdbClient implements ApplicationEventListener<ServiceReadyEvent> {

    @Value("${twitch.client.id}")
    private String clientId;

    @Value("${twitch.client.secret}")
    private String clientSecret;


    public List<Game> jsonQuery(@Size(min = 3) String query) throws InvalidProtocolBufferException, RequestException {
        var igdbWrapper = IGDBWrapper.INSTANCE;
        byte[] bytes = new byte[0];
        bytes = igdbWrapper.apiProtoRequest(Endpoints.GAMES, "search \"" + query + "\"; fields name,rating,summary,url,artworks,first_release_date,artworks.url; limit 10;");
        return GameResult.parseFrom(bytes).getGamesList();
    }

    @PostConstruct
    public void initialize() {
        TwitchAuthenticator twitchAuthenticator = TwitchAuthenticator.INSTANCE;
        TwitchToken twitchToken = twitchAuthenticator.requestTwitchToken(clientId, clientSecret);
        IGDBWrapper igdbWrapper = IGDBWrapper.INSTANCE;
        igdbWrapper.setCredentials(clientId, twitchToken.getAccess_token());
    }

    @Override
    public void onApplicationEvent(ServiceReadyEvent event) {
        initialize();
    }
}
