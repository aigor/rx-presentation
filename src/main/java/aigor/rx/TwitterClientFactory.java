package aigor.rx;

import aigor.rx.twitter.TwitterClient;
import aigor.rx.twitter.TwitterStreamClient;

/**
 * Factory to create Twitter clients
 * Created by aigor on 13.10.16.
 */
public class TwitterClientFactory implements Config {
    public TwitterClient twitterClient(boolean connected){
        TwitterClient client = new TwitterClient(key, secret, System.currentTimeMillis());

        if (connected) {
            client.connect();
        }

        return client;
    }

    public TwitterStreamClient twitterStreamClient(){
        return new TwitterStreamClient(key, secret, token, tokenSecret);
    }
}
