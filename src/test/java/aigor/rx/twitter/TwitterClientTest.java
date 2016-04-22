package aigor.rx.twitter;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

public class TwitterClientTest {
    private static final Logger log= Logger.getLogger(TwitterClientTest.class.getName());

    private final String key = "8ecZrPMbkGwbznWZ9KvOqHjcq2";
    private final String secret = "86hF9PhCPN7gu3kp2aqFSDSIGEGTt1qFeBtTkTCBcVRc8MA6QXr";

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
                .map(user -> new TwitterUser(user, client.getUserInfo(user).getInt("friends_count")))
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("[Took " + (currentTimeMillis() - start) + " ms] Friends count: " + friendsCount);
    }

    @Test
    public void getUserInfoUserRx() throws Exception {
        long start = currentTimeMillis();
        List<TwitterUser> friendsCount = Observable.from(users)
                .subscribeOn(Schedulers.io())
                .flatMap(user ->
                        Observable.zip(
                                Observable.just(user),
                                Observable.fromCallable(() -> client.getUserInfo(user).getInt("friends_count")).subscribeOn(Schedulers.io()),
                                TwitterUser::new
                        )
                )
                .toList()
                .toBlocking()
                .single();

        log.info("[Took " + (currentTimeMillis() - start) + " ms] Friends count: " + friendsCount);

    }

    static class TwitterUser {
        public String name;
        public Integer friends;

        public TwitterUser(String name, Integer friends) {
            this.name = name;
            this.friends = friends;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", friends=" + friends +
                    '}';
        }
    }


}