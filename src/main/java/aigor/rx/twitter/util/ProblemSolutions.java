package aigor.rx.twitter.util;

import aigor.rx.twitter.TwitterClient;
import aigor.rx.twitter.dto.Profile;
import aigor.rx.twitter.dto.Tweet;
import aigor.rx.twitter.dto.UserWithMostPopularWeet;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Solutions for demo problems
 */
public class ProblemSolutions {

    /**
     * Read out user profile and most popular recent tweet (by retweet count).
     * Input params: twitter client, user name
     */
    public Observable<UserWithMostPopularWeet> getUserProfileAndLatestPopularTweet(final TwitterClient client, final String screenName) {
        return Observable.just(screenName).flatMap(u -> {
            Observable<Profile> profile = client.getUserProfile(u).subscribeOn(Schedulers.io());
            Observable<Tweet> tweet = client.getUserRecentTweets(u)
                    .defaultIfEmpty(null)
                    .reduce((t1, t2) -> t1.retweet_count > t2.retweet_count ? t1 : t2)
                    .subscribeOn(Schedulers.io());
            return Observable.zip(profile, tweet, UserWithMostPopularWeet::new);
        });
    }

    /**
     * For stream of users
     *  - get their profile, recent popular tweet (by retweet count)
     *  -
     */

}
