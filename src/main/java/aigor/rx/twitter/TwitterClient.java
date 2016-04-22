package aigor.rx.twitter;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.currentTimeMillis;

/**
 * Client for twitter
 */
public class TwitterClient {
    private static final Logger log = Logger.getLogger(TwitterClient.class.getName());

    static String API_BASE_URL = "https://api.twitter.com/1.1/";
    static String OAUTH_API_BASE_URL = "https://api.twitter.com/oauth2/";

    private final String key;
    private final String secret;

    private volatile Optional<String> authToken = Optional.empty();

    public TwitterClient(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public synchronized TwitterClient connect(){
        if(!authToken.isPresent()) {
            authToken = getAuthToken(key, secret);
        }
        return this;
    }

    public JSONObject getRemainingRequests() {
        long startTime = currentTimeMillis();
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
        } catch (UnirestException e){
            throw new RuntimeException(e);
        } finally {
            logTime("TwitterClient.getUserInfo completed", startTime);
        }
    }

    public JSONObject getUserInfo(String screenName) {
        long startTime = currentTimeMillis();
        try {
            if (authToken.isPresent()) {
                return Unirest.get(API_BASE_URL + "users/show.json")
                        .queryString("screen_name", screenName)
                        .header("Authorization", bearerAuth(authToken.get()))
                        .asJson()
                        .getBody()
                        .getObject();
            } else {
                throw new RuntimeException("Can not connect to twitter");
            }
        } catch (UnirestException e){
            throw new RuntimeException(e);
        } finally {
            logTime("TwitterClient.getUserInfo completed", startTime);
        }
    }

    // --- Supporting methods-------------------------------------------------------------------------------------------

    private static String bearerAuth(String token){
        return "Bearer " + token;
    }

    private static String bearerToken(String key, String secret){
        String token = key + ":" + secret;
        return new String(Base64.getEncoder().encode(token.getBytes()));
    }

    private static Optional<String> getAuthToken(String key, String secret) {
        String token = "Basic " + bearerToken(key, secret) + " Content-Type: application/x-www-form-urlencoded;charset=UTF-8";
        HttpResponse<JsonNode> authorization = null;
        try {
            authorization = Unirest.post(OAUTH_API_BASE_URL + "token")
                    .header("User-Agent", "Rx Demo App")
                    .header("Authorization", token)
                    .field("grant_type", "client_credentials")
                    .asJson();
            if (authorization.getStatus() == 200){
                return Optional.of(authorization.getBody().getObject().get("access_token").toString());
            }
        } catch (UnirestException e) {
            log.log(Level.WARNING, "Error while auth: "+ e.getMessage(), e);
        }
        return Optional.empty();
    }

    private static void logTime(String message, long startTime) {
        log.info(String.format("[%4s ms] [T:%3s] %s", (currentTimeMillis() - startTime), Thread.currentThread().getId(), message));
    }
}
