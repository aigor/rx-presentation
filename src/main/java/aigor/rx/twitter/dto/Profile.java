package aigor.rx.twitter.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Twitter user profile.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {
    public String screen_name;
    public String name;
    public String location;
    public int statuses_count;
    public int friends_count;
    public int followers_count;

    @Override
    public String toString() {
        return "Profile {" +
                "screen_name='" + screen_name + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", statuses_count=" + statuses_count +
                ", friends_count=" + friends_count +
                ", followers_count=" + followers_count +
                '}';
    }
}
