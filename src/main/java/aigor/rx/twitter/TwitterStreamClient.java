package aigor.rx.twitter;

import aigor.rx.twitter.dto.Profile;
import aigor.rx.twitter.dto.Tweet;
import aigor.rx.twitter.dto.UserWithTweet;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import static java.lang.System.currentTimeMillis;

/**
 * Client that allows to listen stream data from Twitter
 *
 * NOTICE:
 * It is better to Observable.using for getting stream in order not initialize twitter client before required time
 */
public class TwitterStreamClient {
    public static final Logger log = Logger.getLogger(TwitterStreamClient.class.getName());

    private final String consumerKey;
    private final String consumerSecret;
    private final String token;
    private final String tokenSecret;

    private PublishSubject<Tweet> tweetStream;

    public TwitterStreamClient(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.tokenSecret = tokenSecret;
        this.tweetStream = PublishSubject.create();
    }

    public Observable<Tweet> getStream(String... tags){
        return Observable.<Tweet, Context>using(() -> {
            long start = currentTimeMillis();
            BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(100000);

            Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
            StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

            hosebirdEndpoint.trackTerms(Lists.newArrayList(tags));
            Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);

            ClientBuilder builder = new ClientBuilder()
                    .name("Rx-Client-01")
                    .hosts(hosebirdHosts)
                    .authentication(hosebirdAuth)
                    .endpoint(hosebirdEndpoint)
                    .processor(new StringDelimitedProcessor(msgQueue)); // optional: use this if you want to process client events

            Client hosebirdClient = builder.build();
            hosebirdClient.connect();
            log.info(MessageFormat.format("Twitter Stream for tags {0} initialized, took {1} ms.", Arrays.asList(tags), currentTimeMillis() - start));
            return new Context(hosebirdClient, msgQueue, new ObjectMapper());
        },
        context -> Observable.create(s -> {
            while (!context.hosebirdClient.isDone() && !s.isUnsubscribed()) {
                String msg = null;
                try {
                    msg = context.msgQueue.take();
                    JsonNode jsonNode = context.om.readTree(msg);
                    if (!jsonNode.has("limit")) {
                        Profile user = context.om.readValue(jsonNode.get("user"), Profile.class);
                        Tweet tweet = context.om.readValue(jsonNode, Tweet.class);
                        tweet.author = user.screen_name;
                        tweet.author_followers = user.followers_count;
                        s.onNext(tweet);
                    } else {
                        log.warning("WE HAVE ACHIEVED RATE LIMIT...");
                    }
                } catch (InterruptedException e) {
                    log.info("Stream was asked to interrupt, using InterruptedException");
                    s.onCompleted();
                } catch (Exception e) {
                    s.onError(e);
                }
            }
            s.onCompleted();
        }),
        context -> {
            long start = currentTimeMillis();
            context.hosebirdClient.stop();
            log.info(MessageFormat.format("Twitter Stream for tags {0} stoped.", Arrays.asList(tags), currentTimeMillis() - start));
        });
    }

    static class Context {
        Client hosebirdClient;
        BlockingQueue<String> msgQueue;
        ObjectMapper om;

        public Context(Client hosebirdClient, BlockingQueue<String> msgQueue, ObjectMapper om) {
            this.hosebirdClient = hosebirdClient;
            this.om = om;
            this.msgQueue = msgQueue;
        }
    }
}
