package aigor.rx.twitter.dto;

/**
 * DTO To represent user and most popular recent tweet
 */
public class UserWithMostPopularWeet {
    public Profile profile;
    public Tweet tweet;

    public UserWithMostPopularWeet(Profile profile, Tweet tweet) {
        this.profile = profile;
        this.tweet = tweet;
    }

    @Override
    public String toString() {
        return "UserWithMostPopularWeet {" +
                "profile=" + profile +
                ", tweet=" + tweet +
                '}';
    }
}
