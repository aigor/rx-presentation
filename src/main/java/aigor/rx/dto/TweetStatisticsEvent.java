package aigor.rx.dto;

/**
 * Created by aigor on 13.10.16.
 */
public class TweetStatisticsEvent {
    public final String type = "tweetStatisticsEvent";
    public Integer receivedTweets;

    public TweetStatisticsEvent(Integer receivedTweets) {
        this.receivedTweets = receivedTweets;
    }
}
