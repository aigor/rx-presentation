package aigor.rx.twitter;

import aigor.rx.twitter.dto.Profile;
import aigor.rx.twitter.dto.Tweet;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

import static aigor.rx.twitter.TwitterClient.logTime;

public class TwitterClientTest extends BaseTest {

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

        logTime(tweets.size() + " tweets found: \n - " + tweets.stream().map(Tweet::toString).collect(Collectors.joining("\n - ")), startTime);
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

        logTime(tweets.size() + " tweets found: \n - " + tweets.stream().map(Tweet::toString).collect(Collectors.joining("\n - ")), startTime);
    }

    @Test
    public void testStream() throws Exception {
        twitterStreamClient.getStream("spacex")
                .take(10)
                .toBlocking()
                .subscribe( n -> logTime("New tweet: " + n, startTime),
                            e -> logTime("ERROR: " + e.getMessage(), startTime),
                            () -> logTime("Stream finished", startTime));
    }
}