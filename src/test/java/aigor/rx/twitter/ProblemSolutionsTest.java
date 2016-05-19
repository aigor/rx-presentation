package aigor.rx.twitter;

import aigor.rx.twitter.dto.Profile;
import aigor.rx.twitter.dto.Tweet;
import aigor.rx.twitter.dto.UserWithTweet;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static aigor.rx.twitter.TwitterClient.logTime;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Samples for presentation
 */
public class ProblemSolutionsTest extends BaseTest {

    @Test public void simpleObservableTest() throws Exception {
        Observable
                .create(s -> {
                    s.onNext("A");
                    s.onNext("B");
                    s.onCompleted();
                })
                .subscribe( m -> log.info("Message received: " + m),
                        e -> log.warning("Error: " + e.getMessage()),
                        () -> log.info("Done!"));
    }

    @Test public void correctlyJoinsHttpResults() throws Exception {
        String testUser = "testUser";
        Profile profile = new Profile("u1", "Name", "USA", 10, 20, 30);
        Tweet tweet1    = new Tweet("text-1", 10, 20, testUser, 30);
        Tweet tweet2    = new Tweet("text-2", 40, 50, testUser, 30);

        TwitterClient client = mock(TwitterClient.class);
        when(client.getUserProfile(testUser)).thenReturn(Observable.just(profile));
        when(client.getUserRecentTweets(testUser)).thenReturn(Observable.just(tweet1, tweet2));

        TestSubscriber<UserWithTweet> testSubscriber = new TestSubscriber<>();
        new ProblemSolutions().getUserProfileAndLatestPopularTweet(client, testUser)
                .subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        assertEquals(singletonList(new UserWithTweet(profile, tweet2)),
                testSubscriber.getOnNextEvents());
    }

    @Test
    public void getUserProfileNumberOfTweets() throws Exception {
        List<UserWithTweet> userWithTweets = Observable.from(users)
                .flatMap(u -> new ProblemSolutions().getUserProfileAndLatestPopularTweet(client, u))
                .toList()
                .toBlocking()
                .single();

        logTime("Users with their most popular tweet: \n - " + userWithTweets.stream().map(UserWithTweet::toString).collect(Collectors.joining("\n - ")), startTime);
    }

    @Test
    public void getUserProfileNumberOfTweets_presentation() throws Exception {
        List<UserWithTweet> userWithTweets = Observable.from(Arrays.asList("jeeconf", "xpinjection"))
                .flatMap(u -> {
                    Observable<Profile> profile = client.getUserProfile(u).subscribeOn(Schedulers.io());
                    Observable<Tweet> tweet = client.getUserRecentTweets(u)
                            .defaultIfEmpty(null)
                            .reduce((t1, t2) -> t1.retweet_count > t2.retweet_count ? t1 : t2)
                            .subscribeOn(Schedulers.io());
                    return Observable.zip(profile, tweet, UserWithTweet::new);
                })
                .toList()
                .toBlocking()
                .single();

        logTime("Users with their most popular tweet: \n - " + userWithTweets.stream().map(UserWithTweet::toString).collect(Collectors.joining("\n - ")), startTime);
    }
}