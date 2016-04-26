package aigor.rx.twitter;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.System.currentTimeMillis;

public class TwitterClientTest {
    private static final Logger log= Logger.getLogger(TwitterClientTest.class.getName());

    private final String key = "ecZrPMbkGwbznWZ9KvOqHjcq2";
    private final String secret = "6hF9PhCPN7gu3kp2aqFSDSIGEGTt1qFeBtTkTCBcVRc8MA6QXr";

    TwitterClient client;

    List<String> users = Arrays.asList("siromaha", "neposuda", "ndrew", "sobakachorna", "PutinsEconomy", "wylsacom");

    @Before
    public void setUp() throws Exception {
        client = new TwitterClient(key, secret);
        client.connect();
    }

    @Test
    public void getUserInfoUserStreams() throws Exception {
        long start = currentTimeMillis();
        List<TwitterUser> friendsCount = users.stream()
                .parallel()
                .map(userName -> new TwitterUser(userName, client.getUserInfo(userName).getInt("friends_count")))
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("[Took " + (currentTimeMillis() - start) + " ms] Friends count: " + friendsCount);
    }

    @Test
    public void getUserInfoUserRx() throws Exception {
        long start = currentTimeMillis();
        List<TwitterUser> friendsCount = Observable.from(users)
                .subscribeOn(Schedulers.io())
                .flatMap(userName ->
                        Observable.zip(
                                Observable.just(userName),
                                Observable.fromCallable(() -> client.getUserInfo(userName).getInt("friends_count"))
                                        .subscribeOn(Schedulers.io()),
                                TwitterUser::new
                        )
                )
                .toList()
                .toBlocking()
                .single();

        log.info("[Took " + (currentTimeMillis() - start) + " ms] Friends count: " + friendsCount);
    }

    @Test
    public void searchForTweets() throws Exception {
        long start = currentTimeMillis();

        List<TwitterUserProfile> profiles = Observable.from(users)
                .subscribeOn(Schedulers.io())
                .flatMap(userName -> client.getUserProfile(userName).subscribeOn(Schedulers.io()))
                .toList()
                .toBlocking()
                .single();

        log.info("[" + (currentTimeMillis() - start) + " ms] Profiles: \n - " +
            profiles.stream()
                .map(TwitterUserProfile::toString)
                .collect(Collectors.joining("\n - ")));


        List<Optional<String>> tweets = Observable.from(users)
                .subscribeOn(Schedulers.io())
                .flatMap(userName ->
                        Observable.fromCallable(() ->
                                (StreamSupport.stream(client.getUserMostPopularTweet(userName)
                                                .getJSONArray("statuses").spliterator(),
                                        false))
                                        .filter(tweet -> tweet instanceof JSONObject)
                                        .map(tweet -> ((JSONObject) tweet).getString("text"))
                                        .findFirst()
                        ).subscribeOn(Schedulers.io())
                )
                .toList()
                .toBlocking()
                .single();

        log.info("[Took " + (currentTimeMillis() - start) + " ms] popular tweets: " + tweets);
    }


    // TASKS:
    // - For username find his number of friends, number of tweets during past week, number of tweets with tags
}