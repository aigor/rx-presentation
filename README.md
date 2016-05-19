# RxJava Applied: Concise Examples where It Shines

This repo is a home for small presentation about RxJava, which is given on [JEEConf 2016](http://jeeconf.com). It contains slides, presented code samples, and some useful links. Presentation description is [here](http://jeeconf.com/program/rxjava-applied-consise-examples-where-it-shines/).

Slides for presentation are hosted on [Slideshare](http://www.slideshare.net/neposuda).

### Short Content of the Presentation

__RxJava short history__
- Nov 17, 2009	Rx for .NET v.1.0 (shipped with .NET 4.0)
- Mar 17, 2010	Reactive Extensions for JS released 
- Aug 15, 2012	Rx for .NET v.2.0
- Feb, 2013		Ben Christensen starts library porting to JVM
- Nov 18, 2014	RxJava v. 1.0.0 
- May 5, 2016   RxJava v. 1.1.5 (latest at the moment)

__Requirements: Stream of Tweets__

__Twitter API__

Twitter Stream API (WebSocket alike):
- Doc: https://dev.twitter.com/streaming/overview
- Library: com.twitter:hbc-core:2.2.0

Twitter REST API:
- GET https://api.twitter.com/1.1/users/show.json?screen_name=jeeconf
- GET https://api.twitter.com/1.1/search/tweets.json?q=from:jeeconf


__Used libraries:__
- [RxJava](https://github.com/ReactiveX/RxJava)
- [Unirest](http://unirest.io/java.html)
- [Twitter Streaming client](https://github.com/twitter/hbc)
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
