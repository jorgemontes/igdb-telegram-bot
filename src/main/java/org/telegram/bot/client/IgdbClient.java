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

    public void jsonQuery() throws RequestException, InvalidProtocolBufferException {
        var igdbWrapper = IGDBWrapper.INSTANCE;
        byte[] bytes = igdbWrapper.apiProtoRequest(Endpoints.GAMES, "fields: *;");
        List<Game> gamesList = GameResult.parseFrom(bytes).getGamesList();
        gamesList.stream().forEach(System.out::println);

    }

    @PostConstruct
    public void initialize() {
        IGDBWrapper igdbWrapper = IGDBWrapper.INSTANCE;
        igdbWrapper.setCredentials(clientId, clientSecret);
    }

    @Override
    public void onApplicationEvent(ServiceReadyEvent event) {
        initialize();
    }
}
