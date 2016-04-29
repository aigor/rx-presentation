package aigor.rx.twitter;

import aigor.rx.twitter.dto.Profile;
import aigor.rx.twitter.dto.Tweet;
import aigor.rx.twitter.dto.UserWithMostPopularWeet;
import aigor.rx.twitter.util.LogMessageFormat;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static aigor.rx.twitter.TwitterClient.logTime;
import static java.lang.System.currentTimeMillis;

public class TwitterClientTest {
    private static final Logger log = Logger.getLogger(TwitterClientTest.class.getName());

    private final String key = "ecZrPMbkGwbznWZ9KvOqHjcq2";
    private final String secret = "6hF9PhCPN7gu3kp2aqFSDSIGEGTt1qFeBtTkTCBcVRc8MA6QXr";

    private long startTime;

    TwitterClient client;

    List<String> users = Arrays.asList("siromaha", "neposuda", "ndrew", "sobakachorna", "PutinsEconomy", "wylsacom");

    @Before
    public void setUp() throws Exception {
        LogMessageFormat.alterLogging();
        startTime = currentTimeMillis();
        client = new TwitterClient(key, secret, startTime);
        client.connect();
    }

    @Test
    public void getUserProfiles() throws Exception {
        List<Profile> profiles = Observable.from(users)
                .subscribeOn(Schedulers.io())
                .flatMap(userName -> client.getUserProfile(userName).subscribeOn(Schedulers.io()))
                .toList()
                .toBlocking()
                .single();

        logTime("Profiles: \n - " + profiles.stream().map(Profile::toString).collect(Collectors.joining("\n - ")), startTime);
    }

    @Test
    public void getUserTweets() throws Exception {
        List<Tweet> tweets = Observable.from(users)
                .subscribeOn(Schedulers.io())
                .flatMap(userName -> client.getUserRecentTweets(userName).subscribeOn(Schedulers.io()))
                .toList()
                .toBlocking()
                .single();

        logTime(tweets.size() + " tweets: \n - " + tweets.stream().map(Tweet::toString).collect(Collectors.joining("\n - ")), startTime);
    }

    @Test
    public void getUserTweetsWithErrorHandling() throws Exception {
        List<Tweet> tweets = Observable.from(users)
                .subscribeOn(Schedulers.io())
                .flatMap(userName -> client.getUserRecentTweets(userName)
                        .retry((i, e) -> {
                            logTime("Error " + i + "-th time on getting tweets for: " + userName + ", would retry, error: " + e.getMessage(), startTime);
                            if (i >= 3) {
                                logTime("Due to errors we can not retrieve tweets for user: " + userName, startTime);
                            }
                            return i < 3;
                        })
                        .onErrorResumeNext(Observable.empty())
                        .subscribeOn(Schedulers.io()))
                .toList()
                .toBlocking()
                .single();

        logTime(tweets.size() + " tweets: \n - " + tweets.stream().map(Tweet::toString).collect(Collectors.joining("\n - ")), startTime);
    }

    @Test
    public void getUserProfileNumberOfTweets() throws Exception {
        List<UserWithMostPopularWeet> userWithTweets = Observable.from(users)
                .flatMap(u -> new ProblemSolutions().getUserProfileAndLatestPopularTweet(client, u))
                .toList()
                .toBlocking()
                .single();

        logTime("Users with their most popular tweet: \n - " + userWithTweets.stream().map(UserWithMostPopularWeet::toString).collect(Collectors.joining("\n - ")), startTime);
    }

    @Test
    public void getUserAnfLatestPopularTweet() throws Exception {

    }

    // TASKS:
    // - For username find his number of friends, number of tweets during past week, number of tweets with tags
}