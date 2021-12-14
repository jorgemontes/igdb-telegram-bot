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
import java.util.List;

@Singleton
public class IgdbClient implements ApplicationEventListener<ServiceReadyEvent> {

    @Value("${twitch.client.id}")
    private String clientId;

    @Value("${twitch.client.secret}")
    private String clientSecret;


    public List<Game> jsonQuery(String query) throws InvalidProtocolBufferException {

        var igdbWrapper = IGDBWrapper.INSTANCE;
        byte[] bytes = new byte[0];
        try {
            bytes = igdbWrapper.apiProtoRequest(Endpoints.GAMES, "search \"" + query + "\"; fields name,rating,summary,artworks,artworks.url;");
        } catch (RequestException e) {
            e.printStackTrace();
        }
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
