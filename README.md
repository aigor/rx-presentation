# RxJava Applied: Concise Examples where It Shines

[![Build Status](https://travis-ci.org/aigor/rx-presentation.svg?branch=master)](https://travis-ci.org/aigor/rx-presentation)

This repo is a home for small presentation about RxJava, which was given on [JEEConf 2016](http://jeeconf.com). It contains slides, presented code samples, and some useful links. Presentation description is [here](http://jeeconf.com/program/rxjava-applied-consise-examples-where-it-shines/).

#### [Presentation slides are here, on Slideshare](http://www.slideshare.net/neposuda/rxjava-applied).

## Short Content of the Presentation

#### Simplified schema of modern applications
![modern applications](https://raw.githubusercontent.com/aigor/rx-presentation/master/slides/diagrams/TodayApplication.png)

#### RxJava short history

| Date          | What happended                                    | 
| ------------- | ------------------------------------------------- |
| Nov 17, 2009	| Rx for .NET v.1.0 (shipped with .NET 4.0)         | 
| Mar 17, 2010	| Reactive Extensions for JS released               | 
| Aug 15, 2012	| Rx for .NET v.2.0                                 | 
| Feb, 2013		| Ben Christensen starts library porting to JVM     | 
| Nov 18, 2014	| RxJava v. 1.0.0                                   | 
| May 5, 2016   | RxJava v. 1.1.5 (latest at the moment)            | 

#### Requirements: Stream of Tweets

```
Having stream of new tweets (based on keywords):
- Track and report most followed tweet author in stream
- Track and report most retweeted tweet of most popular user
```

![Solution mockUp](https://raw.githubusercontent.com/aigor/rx-presentation/master/slides/diagrams/TwitterBreeze.png)

#### Used Twitter API

Twitter Stream API (WebSocket alike):
- Doc: https://dev.twitter.com/streaming/overview
- Library: ```com.twitter:hbc-core:2.2.0```

Twitter REST API ([Documentation](https://dev.twitter.com/rest/public)):
- ```GET https://api.twitter.com/1.1/users/show.json?screen_name=jeeconf```
- ```GET https://api.twitter.com/1.1/search/tweets.json?q=from:jeeconf```

#### Entities used in solution

```java  
class Tweet {
    String text;
    int favorite_count;
    String author;
    int author_followers;
}
```

```java
class Profile {
   String screen_name;
   String name;
   String location;
   int statuses_count;
   int followers_count;
}
```

```java
class UserWithTweet {
   Profile profile;
   Tweet tweet;
}
```

#### Complete solution diagram

![Complete solution diagram](https://raw.githubusercontent.com/aigor/rx-presentation/master/slides/diagrams/RxExample-2.png)

#### Getting user profile synchronously
```java
Profile getUserProfile(String screenName) {       
      ObjectMapper om = new ObjectMapper();
      return (Profile) om.readValue(om.readTree(
            Unirest.get(API_BASE_URL + "users/show.json")
                   .queryString("screen_name", screenName)
                   .header("Authorization", bearerAuth(authToken.get()))
                   .asString()
                   .getBody()),
            Profile.class);
}
```

#### Getting user profile asynchronously
```java
Observable<Profile> getUserProfile(String screenName) {
   if (authToken.isPresent()) {
       return Observable.fromCallable(() -> {
           ObjectMapper om = new ObjectMapper();
           return (Profile) om.readValue(om.readTree(
                   Unirest.get(API_BASE_URL + "users/show.json")
                           .queryString("screen_name", screenName)
                           .header("Authorization", bearerAuth(authToken.get()))
                           .asString()
                  .getBody()),
                   Profile.class);
       }).doOnCompleted(() -> log("getUserProfile completed for: " + screenName));
   } else {
       return Observable.error(new RuntimeException("Can not connect to twitter"));
   }
}
```

#### Solution diagram for: getUserAndPopularTweet(userName)

![getUserAndPopularTweet](https://raw.githubusercontent.com/aigor/rx-presentation/master/slides/diagrams/RxExample-1.png)

```java
Observable<UserWithTweet> getUserAndPopularTweet(String author){
    return Observable.just(author)
    .flatMap(u -> {
        Observable<Profile> profile = client.getUserProfile(u)
            .subscribeOn(Schedulers.io());
        Observable<Tweet> tweet = client.getUserRecentTweets(u)
            .defaultIfEmpty(null)
            .reduce((t1, t2) ->
                t1.retweet_count > t2.retweet_count ? t1 : t2)
            .subscribeOn(Schedulers.io());
        return Observable.zip(profile, tweet, UserWithTweet::new);
    });
}
```

#### Tweat stream subscription (most popular user)
```java
streamClient.getStream("RxJava", "JEEConf", "Java", "Trump")
    .scan((u1, u2) -> u1.author_followers > u2.author_followers ? u1 : u2)
    .distinctUntilChanged()
    .map(p -> p.author)
    .flatMap(name -> getUserAndPopularTweet(name))
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.immediate())
    .subscribe(p -> log.info("The most popular tweet of user " 
                             + p.profile.name + ": " + p.tweet));
```

Same solution but with extended method: getUserAndPopularTweet(name)
```java
streamClient.getStream("RxJava", "JEEConf", "Java", "Trump")
    .scan((u1, u2) -> u1.author_followers > u2.author_followers ? u1 : u2)
    .distinctUntilChanged()
    .map(p -> p.author)
    .flatMap(name -> {
        Observable<Profile> profile = client.getUserProfile(name)
            .subscribeOn(Schedulers.io());
        Observable<Tweet> tweet = client.getUserRecentTweets(name)
            .defaultIfEmpty(null)
            .reduce((t1, t2) -> 
                t1.retweet_count > t2.retweet_count ? t1 : t2)
            .subscribeOn(Schedulers.io());
        return Observable.zip(profile, tweet, UserWithTweet::new);
    })
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.immediate())
    .subscribe(p -> log.info("The most popular tweet of user " 
                             + p.profile.name + ": " + p.tweet));
```

#### Short conclusions

Pitfalls:
- API is big (150+ methods to remember)
- Requires to understand underlying magic
- Hard to debug
- Don’t forget about back pressure

Strength:
- It is functional, it is reactive* 
- Good for integration scenarios
- Allows to control execution threads
- Easy to compose workflows
- Easy to integrate into existing solutions
- Easy to test

#### Test code example
```java
@Test public void correctlyJoinsHttpResults() throws Exception {
   String testUser = "testUser";
   Profile profile = new Profile("u1", "Name", "USA", 10, 20, 30);
   Tweet tweet1    = new Tweet("text-1", 10, 20, testUser, 30);
   Tweet tweet2    = new Tweet("text-2", 40, 50, testUser, 30);

   TwitterClient client = mock(TwitterClient.class);
   when(client.getUserProfile(testUser)).thenReturn(Observable.just(profile));
   when(client.getUserRecentTweets(testUser)).thenReturn(Observable.just(tweet1, tweet2));      

   TestSubscriber<UserWithTweet> testSubscriber = new TestSubscriber<>();
   new Solutions().getUserAndPopularTweet(client, testUser).subscribe(testSubscriber);
   testSubscriber.awaitTerminalEvent();
   assertEquals(singletonList(new UserWithTweet(profile, tweet2)),
           testSubscriber.getOnNextEvents());
}
```

#### Used libraries
- [RxJava](https://github.com/ReactiveX/RxJava)
- [Unirest](http://unirest.io/java.html)
- [Jackson JSON Processor](http://wiki.fasterxml.com/JacksonHome)
- [Twitter Streaming Client: twitter-hbc](https://github.com/twitter/hbc)
- [Mockito](http://mockito.org)

## If You Want to Learn More about Reactive Programming & RxJava

Good starting points:
- [Reactive manifesto](http://www.reactivemanifesto.org)
- [Collection of open source projects & documentation, including Netflix JavaRx](http://reactivex.io)

Recommended tutorials and courses:
- [Dan Lew: Grokking RxJava](http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/)
- [Intro To RxJava](https://github.com/Froussios/Intro-To-RxJava)
- [Coursera: Principles of Reactive Programming](https://www.coursera.org/course/reactive)

Recommended videos:
- [José Paumard: Java 8 Stream and RxJava comparison: patterns and performances](https://youtu.be/fabN6HNZ2qY)
- [Artur Glier: Learn you some Rx for the greater good](https://www.youtube.com/watch?v=BujWQSjtplc)
- [Ross Hambrick: RxJava and Retrolambda Making Android | Devnexus 2015](https://www.youtube.com/watch?v=vRl3u1I9v2M)
- [Li Haoyi: Fun Functional-Reactive Programming with Scala.Rx](https://www.youtube.com/watch?v=i9mPUU1gu_8)
- [Ben Christensen: Functional Reactive Programming with RxJava, Netflix](https://www.youtube.com/watch?v=_t06LRX0DV0)
- [Erik Meijer: A Playful Introduction to Rx](https://www.youtube.com/watch?v=WKore-AkisY)
- [Chris Richardson: Futures and Rx Observables: powerful abstractions for consuming web services asynchronously](https://www.youtube.com/watch?v=aZkwIA4k2xU)
- [Roland Kuhn: Reactive Design Patterns](https://www.youtube.com/watch?v=nSfXcSWq0ug)

Recommended articles:
- [Concurrency in RxJava (RU)](https://habrahabr.ru/company/rambler-co/blog/280388/)

Good Presentations:
- [RxJava - introduction & design](http://www.slideshare.net/allegrotech/rxjava-introduction-context)
- [Building Scalable Stateless Applications with RxJava](http://www.slideshare.net/rickbwarren/building-scalable-stateless-applications-with-rx-java)
- [Reactive Programming with RxJava for Efficient Data Access](http://www.slideshare.net/Couchbase/reactive-programmingrxjavaefficientdata-benchristensenmichaelnitschinger)
- [Simon Baslé: Practical RxJava](http://www.slideshare.net/SpringCentral/practical-rxjava)
- [Java 8 Streaming API vs RxJava](http://www.slideshare.net/jpaumard/java-8-stream-api-and-rxjava-comparison)

Reactive programming for Scala:
- [RxScala - RxJava binding](http://reactivex.io/rxscala/)
- [Scientific paper: Ingo Maier Martin Odersky: Deprecating the Observer Pattern with Scala.React](http://infoscience.epfl.ch/record/176887/files/DeprecatingObservers2012.pdf)
- [Scala.Rx: experimental feature of Scala SDK](https://github.com/lihaoyi/scala.rx)

Reactive programming for other platforms:
- [.NET F#: Functional Reactive Programming](https://fsharpforfunandprofit.com/posts/concurrency-reactive/)
- [RxSwift: Functional Reactive Programming with RxSwift](https://realm.io/news/slug-max-alexander-functional-reactive-rxswift/)
