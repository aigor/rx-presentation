package aigor.rx.twitter.dto;

/**
 * DTO to represent user and tweet
 */
public class UserWithTweet {
    public Profile profile;
    public Tweet tweet;

    public UserWithTweet(Profile profile, Tweet tweet) {
        this.profile = profile;
        this.tweet = tweet;
    }

    @Override
    public String toString() {
        return "User+Tweet {" +
                "profile=" + profile +
                ", tweet=" + tweet +
                '}';
    }
}
