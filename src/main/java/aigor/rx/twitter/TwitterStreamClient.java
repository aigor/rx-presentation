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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Client that allows to listen stream data from Twitter
 * 
 * TODO: Use Observable.using here !
 */
public class TwitterStreamClient {
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
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
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
        ObjectMapper om = new ObjectMapper();

        Observable.<Tweet>create(s -> {
            hosebirdClient.connect();

            while (!hosebirdClient.isDone() && !s.isUnsubscribed()) {
                String msg = null;
                try {
                    msg = msgQueue.take();
                    JsonNode jsonNode = om.readTree(msg);
                    if (!jsonNode.has("limit")) {
                        Profile user = om.readValue(jsonNode.get("user"), Profile.class);
                        Tweet tweet = om.readValue(jsonNode, Tweet.class);
                        tweet.author = user.screen_name;
                        tweet.author_followers = user.followers_count;
                        s.onNext(tweet);
                    } else {
                        System.out.println("WE HAVE ACHIEVED RATE LIMIT...");
                    }
                } catch (Exception e) {
                    s.onError(e);
                }
            }
            s.onCompleted();
            hosebirdClient.stop();
        }).subscribeOn(Schedulers.io())
                .subscribe(tweetStream);

        return tweetStream;
    }
}
