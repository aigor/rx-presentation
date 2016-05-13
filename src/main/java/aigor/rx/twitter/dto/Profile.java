package aigor.rx.twitter.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * DTO to represent Twitter user profile.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        if (name != null ? !name.equals(profile.name) : profile.name != null) return false;
        if (screen_name != null ? !screen_name.equals(profile.screen_name) : profile.screen_name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = screen_name != null ? screen_name.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
