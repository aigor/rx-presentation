package aigor.rx.twitter;

import aigor.rx.twitter.dto.Profile;
import aigor.rx.twitter.dto.Tweet;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import rx.Observable;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.System.currentTimeMillis;

/**
 * Client for Twitter
 * User Twitter REST API
 *
 * Uses simple implementation of OAuth 2 protocol
 * HTTP requests are done using Unirest library
 *
 * REST API specification: https://dev.twitter.com/rest/public
 * OAuth 2 Twitter protocol: https://dev.twitter.com/oauth/application-only
 */
public class TwitterClient {
    private static final Logger log = Logger.getLogger(TwitterClient.class.getSimpleName());

    static String API_BASE_URL          = "https://api.twitter.com/1.1/";
    static String OAUTH_API_BASE_URL    = "https://api.twitter.com/oauth2/";

    private final String key;
    private final String secret;
    private final long startTime;

    private volatile Optional<String> authToken = Optional.empty();

    public TwitterClient(String key, String secret) {
        this.key = key;
        this.secret = secret;
        startTime = currentTimeMillis();
    }

    public TwitterClient(String key, String secret, long startTime) {
        this.key = key;
        this.secret = secret;
        this.startTime = startTime;
    }

    public synchronized TwitterClient connect() {
        if (!authToken.isPresent()) {
            authToken = getAuthToken(key, secret);
        }
        return this;
    }

    public Observable<Profile> getUserProfile(String screenName) {
        if (authToken.isPresent()) {
            return Observable.fromCallable(() -> {
                ObjectMapper om = new ObjectMapper();
                debugTime("getUserProfile started for: " + screenName, startTime);
                return om.readValue(om.readTree(
                        Unirest.get(API_BASE_URL + "users/show.json")
                                .queryString("screen_name", screenName)
                                .header("Authorization", bearerAuth(authToken.get()))
                                .asString()
                                .getBody()),
                        Profile.class);
            }).doOnCompleted(() -> debugTime("getUserProfile for completed for: " + screenName, startTime));
        } else {
            return Observable.error(new RuntimeException("Can not connect to twitter"));
        }
    }

    public Observable<Tweet> getUserRecentTweets(final String screenName) {
        if (authToken.isPresent()) {
            return Observable.fromCallable(() -> {
                ObjectMapper om = new ObjectMapper();
                debugTime("getUserRecentTweets started for: " + screenName, startTime);
                JSONObject searchResults = Unirest.get(API_BASE_URL + "search/tweets.json")
                        .header("Authorization", bearerAuth(authToken.get()))
                        .queryString("q", "from:" + screenName)
                        .asJson()
                        .getBody()
                        .getObject();

                return StreamSupport.stream(searchResults.getJSONArray("statuses").spliterator(), false)
                        .filter(e -> e instanceof JSONObject)
                        .map(o -> {
                            try {
                                return om.readValue(om.readTree(o.toString()), Tweet.class);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).collect(Collectors.toList());
            })
            .flatMap(Observable::from)
            .doOnCompleted(() -> debugTime("getUserRecentTweets for completed for: " + screenName, startTime));
        } else {
            return Observable.error(new RuntimeException("Can not connect to twitter"));
        }
    }

    public JSONObject getRemainingRequests() {
        try {
            if (authToken.isPresent()) {
                return Unirest.get(API_BASE_URL + "application/rate_limit_status.json")
                        .header("Authorization", bearerAuth(authToken.get()))
                        .asJson()
                        .getBody()
                        .getObject();
            } else {
                throw new RuntimeException("Can not connect to twitter");
            }
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        } finally {
            debugTime("getUserInfo completed", startTime);
        }
    }

    // --- Supporting methods-------------------------------------------------------------------------------------------

    private static void generateErrorWithProbability(int prob){
        int randomInt = new Random().nextInt(100);
        if (randomInt < prob){
            throw new RuntimeException("communication error " + randomInt);
        }
    }

    private static String bearerAuth(String token) {
        return "Bearer " + token;
    }

    private static String bearerToken(String key, String secret) {
        String token = key + ":" + secret;
        return new String(Base64.getEncoder().encode(token.getBytes()));
    }

    private static Optional<String> getAuthToken(String key, String secret) {
        String token = "Basic " + bearerToken(key, secret) + " Content-Type: application/x-www-form-urlencoded;charset=UTF-8";
        HttpResponse<JsonNode> authorization;
        try {
            authorization = Unirest.post(OAUTH_API_BASE_URL + "token")
                    .header("User-Agent", "Rx Demo App")
                    .header("Authorization", token)
                    .field("grant_type", "client_credentials")
                    .asJson();
            if (authorization.getStatus() == 200) {
                return Optional.of(authorization.getBody().getObject().get("access_token").toString());
            }
        } catch (UnirestException e) {
            log.log(Level.WARNING, "Error while auth: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public static void logTime(String message, long startTime) {
        log.info(String.format("[%4s ms] %s", (currentTimeMillis() - startTime), message));
    }
    public static void debugTime(String message, long startTime) {
        log.fine(String.format("[%4s ms] %s", (currentTimeMillis() - startTime), message));
    }

}
