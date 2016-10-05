package aigor.rx.twitter;

import aigor.rx.twitter.dto.Profile;
import aigor.rx.twitter.dto.Tweet;
import aigor.rx.twitter.dto.UserWithTweet;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.List;
import java.util.concurrent.TimeUnit;
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

    @Ignore
    @Test
    public void solutionUsedInPresentationTest() throws Exception {
        Subscription subscription = streamClient.getStream("RxJava", "JEEConf", "Java", "Trump")
                .scan((u1, u2) -> u1.author_followers > u2.author_followers ? u1 : u2)
                .distinctUntilChanged()
                .map(p -> p.author)
                .flatMap(name -> {
                    Observable<Profile> profile = client.getUserProfile(name)
                            .subscribeOn(Schedulers.io());
                    Observable<Tweet> tweet = client.getUserRecentTweets(name)
                            .defaultIfEmpty(null)
                            .reduce((t1, t2) -> t1.retweet_count > t2.retweet_count ? t1 : t2)
                            .subscribeOn(Schedulers.io());
                    return Observable.zip(profile, tweet, UserWithTweet::new);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(p -> log.info("The most popular tweet of user " + p.profile.name + ": " + p.tweet));

        // Sleep some time in order to give other threads time to work
        Thread.sleep(60_000);
        subscription.unsubscribe();
        Thread.sleep(2_000);

    }

    // -----------------------------------------------------------------------------------------------------------------
    // For stream of tweets track most popular user, his most popular tweet, amount of tweets per 10 seconds
    // -----------------------------------------------------------------------------------------------------------------
    @Ignore
    @Test
    public void testStream() throws Exception {
        Observable<Tweet> tweetStream = streamClient.getStream("RxJava", "JEEConf", "Java", "Trump");

        PublishSubject<Tweet> hostTweetStream = PublishSubject.create();
        Subscription subscription = tweetStream
                .subscribeOn(Schedulers.io())
                .subscribe(hostTweetStream);

        hostTweetStream
                .window(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(window ->
                                window.count()
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(n -> log.info("In 10 sec received tweets: " + n))
                );

        Observable<Tweet> tweetsFromMorePopularUsers = hostTweetStream
                .scan((u1, u2) -> u1.author_followers > u2.author_followers ? u1 : u2)
                .distinctUntilChanged();

        tweetsFromMorePopularUsers
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(p -> log.info("New the most followed user: " + p.author
                        + " (followed by: " + p.author_followers
                        + ") with tweet: " + p.text)
                );

        tweetsFromMorePopularUsers
                .flatMap(t -> new ProblemSolutions().getUserProfileAndLatestPopularTweet(client, t.author))
                .subscribeOn(Schedulers.io())
                .subscribe( p ->
                        log.info("The most popular tweet of user "
                                + p.profile.name + ": " + p.tweet)
                );

        // Sleep some time in order to give other threads time to work
        Thread.sleep(15_000);
        subscription.unsubscribe();
        Thread.sleep(2_000);
    }

    @Ignore
    @Test
    public void testStreamInParallel() throws Exception {
        String[] words = {"RxJava", "JEEConf", "Java", "Obama", "Putin", "Russia", "USA", "Canada"};

        for (int threadId = 0; threadId < words.length; threadId++) {
            final String word = words[threadId];
            new Thread(() -> {
                Observable<Tweet> tweetStream = streamClient.getStream(word);

                PublishSubject<Tweet> hostTweetStream = PublishSubject.create();
                Subscription subscription = tweetStream
                        .subscribeOn(Schedulers.io())
                        .subscribe(hostTweetStream);

                hostTweetStream
                        .window(10, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .subscribe(window ->
                                window.count()
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(n -> log.info("["+ word +"] In 10 sec received tweets: " + n))
                        );

                Observable<Tweet> tweetsFromMorePopularUsers = hostTweetStream
                        .scan((u1, u2) -> u1.author_followers > u2.author_followers ? u1 : u2)
                        .distinctUntilChanged();

                tweetsFromMorePopularUsers
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .subscribe(p -> log.info("["+ word +"] New the most followed user: " + p.author
                                + " (followed by: " + p.author_followers
                                + ") with tweet: " + p.text)
                        );

                tweetsFromMorePopularUsers
                        .flatMap(t -> new ProblemSolutions().getUserProfileAndLatestPopularTweet(client, t.author))
                        .subscribeOn(Schedulers.io())
                        .subscribe(p ->
                                log.info("["+ word +"] The most popular tweet of user "
                                        + p.profile.name + ": " + p.tweet)
                        );
                try {
                    // Sleep some time in order to give other threads time to work
                    Thread.sleep(15_000);
                    subscription.unsubscribe();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        }

        Thread.sleep(20_000);
    }

}