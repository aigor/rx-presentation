package aigor.rx.twitter;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.*;
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
        Map<String, Integer> friendsCount = users.stream()
                .parallel()
                .collect(Collectors.toMap(  user -> user,
                                            user -> client.getUserInfo(user).getInt("friends_count"))
        );
        log.info("Friends count: " + friendsCount + ", took " + (currentTimeMillis() - start) + "ms.");
    }

    @Test
    public void getUserInfoUserRx() throws Exception {
        long start = currentTimeMillis();
        Iterator<Integer> friendsCount = Observable.from(users)
                .subscribeOn(Schedulers.io())
                .flatMap(user -> Observable.just(client.getUserInfo(user).getInt("friends_count")))
                .take(users.size())
                .toBlocking()
                .getIterator();

        List<Integer> list = new ArrayList<>();
        friendsCount.forEachRemaining(list::add);

        log.info("Friends count: " + list + ", took " + (currentTimeMillis() - start) + "ms.");

    }


}