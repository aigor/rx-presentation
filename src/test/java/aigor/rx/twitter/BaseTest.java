package aigor.rx.twitter;

import aigor.rx.twitter.util.LogMessageFormat;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.currentTimeMillis;

/**
 * Base test - setup
 */
class BaseTest {
    private final String key = "ecZrPMbkGwbznWZ9KvOqHjcq2";
    private final String secret = "6hF9PhCPN7gu3kp2aqFSDSIGEGTt1qFeBtTkTCBcVRc8MA6QXr";

    private final String token = "199894838-ba1xZ7tZy5jA5cSjAvsZvCfdnDSHxwtwzQ1cbE2R";
    private final String tokenSecret = "PUEaq5bpoelBbGze4WIPPEWKFiie305EATHzCIx8PHIya";

    long startTime;
    TwitterClient client;
    TwitterStreamClient twitterStreamClient;

    static final List<String> users = Arrays.asList("siromaha", "neposuda", "ndrew", "sobakachorna", "PutinsEconomy", "wylsacom");

    @Before
    public void setUp() throws Exception {
        LogMessageFormat.alterLogging();
        startTime = currentTimeMillis();
        client = new TwitterClient(key, secret, startTime);
        client.connect();

        twitterStreamClient = new TwitterStreamClient(key, secret, token, tokenSecret);
    }
}
