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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWithTweet that = (UserWithTweet) o;

        if (profile != null ? !profile.equals(that.profile) : that.profile != null) return false;
        return tweet != null ? tweet.equals(that.tweet) : that.tweet == null;

    }

    @Override
    public int hashCode() {
        int result = profile != null ? profile.hashCode() : 0;
        result = 31 * result + (tweet != null ? tweet.hashCode() : 0);
        return result;
    }
}
