package aigor.rx.twitter;

import aigor.rx.twitter.util.LogMessageFormat;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.System.currentTimeMillis;

/**
 * ATTENTION:
 * - THIS TWITTER APP KEYS HAVE READ-ONLY ACCESS AND WOULD BE INVALIDATED AFTER OCT 15, 2016.
 * - THIS CONNECTIVITY KEYS ARE USED ONLY FOR DEMONSTRATION PURPOSES.
 */
abstract class BaseTest {
    public static final Logger log = Logger.getLogger(BaseTest.class.getName());

    private final String key = "ecZrPMbkGwbznWZ9KvOqHjcq2";
    private final String secret = "6hF9PhCPN7gu3kp2aqFSDSIGEGTt1qFeBtTkTCBcVRc8MA6QXr";

    private final String token = "199894838-ba1xZ7tZy5jA5cSjAvsZvCfdnDSHxwtwzQ1cbE2R";
    private final String tokenSecret = "PUEaq5bpoelBbGze4WIPPEWKFiie305EATHzCIx8PHIya";

    long startTime;
    TwitterClient client;
    TwitterStreamClient streamClient;

    static final List<String> users = Arrays.asList("siromaha", "neposuda", "ndrew", "sobakachorna", "PutinsEconomy", "wylsacom");

    @Before
    public void setUp() throws Exception {
        LogMessageFormat.alterLogging();
        startTime = currentTimeMillis();
        client = new TwitterClient(key, secret, startTime);
        client.connect();

        streamClient = new TwitterStreamClient(key, secret, token, tokenSecret);
    }
}
