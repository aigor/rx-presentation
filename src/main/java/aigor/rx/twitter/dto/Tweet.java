package aigor.rx.twitter.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * DTO to represent tweet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {
    public String text;
    public int favorite_count;
    public int retweet_count;
    public String author;
    public int author_followers;

    @Override
    public String toString() {
        return "Tweet {" +
                "text='" + text + '\'' +
                ", favorite_count=" + favorite_count +
                ", retweet_count=" + retweet_count +
                '}';
    }
}
