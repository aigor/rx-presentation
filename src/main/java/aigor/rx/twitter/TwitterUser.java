package aigor.rx.twitter;

/**
 * Class to represent twitter user
 */
class TwitterUser {
    private String name;
    private Integer friends;

    public TwitterUser(String name, Integer friends) {
        this.name = name;
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", friends=" + friends +
                '}';
    }

    public String getName() {
        return name;
    }

    public Integer getFriends() {
        return friends;
    }
}
