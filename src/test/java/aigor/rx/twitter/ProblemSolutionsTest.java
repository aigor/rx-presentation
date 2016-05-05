package aigor.rx.twitter;

import aigor.rx.twitter.dto.Profile;
import aigor.rx.twitter.dto.Tweet;
import aigor.rx.twitter.dto.UserWithMostPopularWeet;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

import static aigor.rx.twitter.TwitterClient.logTime;

/**
 * Sample for presentation
 */
public class ProblemSolutionsTest extends BaseTest {

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
    public void getUserProfileNumberOfTweets_presentation() throws Exception {
        List<UserWithMostPopularWeet> userWithTweets = Observable.just("siromaha")
                .flatMap(u -> {
                    Observable<Profile> profile = client.getUserProfile(u).subscribeOn(Schedulers.io());
                    Observable<Tweet> tweet = client.getUserRecentTweets(u)
                            .defaultIfEmpty(null)
                            .reduce((t1, t2) -> t1.retweet_count > t2.retweet_count ? t1 : t2)
                            .subscribeOn(Schedulers.io());
                    return Observable.zip(profile, tweet, UserWithMostPopularWeet::new);
                })
                .toList()
                .toBlocking()
                .single();

        logTime("Users with their most popular tweet: \n - " + userWithTweets.stream().map(UserWithMostPopularWeet::toString).collect(Collectors.joining("\n - ")), startTime);
    }
}