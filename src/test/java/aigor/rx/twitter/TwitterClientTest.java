package aigor.rx.twitter;

import aigor.rx.twitter.dto.Profile;
import aigor.rx.twitter.dto.Tweet;
import aigor.rx.twitter.dto.UserWithTweet;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static aigor.rx.twitter.TwitterClient.logTime;
import static java.util.logging.Level.WARNING;

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

    // For stream of tweets track most popular user, his most popular tweet, amount of tweets per minute

    @Test
    public void testStream() throws Exception {
        Observable<UserWithTweet> tweetStream = twitterStreamClient.getStream("победа");

        tweetStream
                .window(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(window ->
                                window.count()
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(n -> log.info("In 10 sec received tweets: " + n))
                );

        tweetStream
                .map(u -> u.profile)
                .scan((u1, u2) -> u1.followers_count > u2.followers_count ? u1: u2)
                .distinctUntilChanged()
                .flatMap(p -> new ProblemSolutions().getUserProfileAndLatestPopularTweet(client, p.screen_name))
                .subscribeOn(Schedulers.io())
                .subscribe( p -> log.info("Most popular User so far: " + p),
                            e -> log.log(WARNING, "Exception received", e));

        tweetStream
                .toBlocking()
                .subscribe( n -> { /* logTime("New tweet: " + n, startTime) */ },
                            e -> log.log(WARNING, "Exception received", e));
    }
}