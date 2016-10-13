package aigor.rx.twitter.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * DTO to represent tweet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {
    public final String type = "tweet";

    public String text;
    public int favorite_count;
    public int retweet_count;
    public String author;
    public int author_followers;

    public Tweet() {
    }

    public Tweet(String text, int favorite_count, int retweet_count, String author, int author_followers) {
        this.text = text;
        this.favorite_count = favorite_count;
        this.retweet_count = retweet_count;
        this.author = author;
        this.author_followers = author_followers;
    }

    @Override
    public String toString() {
        return "Tweet {" +
                "text='" + text + '\'' +
                ", favorite_count=" + favorite_count +
                ", retweet_count=" + retweet_count +
                '}';
    }
}
