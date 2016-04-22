package aigor.rx.twitter;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

public class TwitterClientTest {
    private static final Logger log= Logger.getLogger(TwitterClientTest.class.getName());

    private final String key = "8ecZrPMbkGwbznWZ9KvOqHjcq2";
    private final String secret = "86hF9PhCPN7gu3kp2aqFSDSIGEGTt1qFeBtTkTCBcVRc8MA6QXr";

    TwitterClient client;

    @Before
    public void setUp() throws Exception {
        client = new TwitterClient(key, secret);
        client.connect();
    }

    @Test
    public void getUserInfo() throws Exception {
        long start = currentTimeMillis();
        List<String> users = Arrays.asList("siromaha", "neposuda", "ndrew", "sobakachorna", "PutinsEconomy");
        Map<String, Integer> friends_count = users.stream()
                .parallel()
                .collect(Collectors.toMap(user -> user, user ->
                client.getUserInfo(user).getInt("friends_count"))
        );
        log.info("Friends count: " + friends_count + ", took " + (currentTimeMillis() - start) + "ms.");

    }


}