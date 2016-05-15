# RxJava Applied: Concise Examples where It Shines

This repo is a home for small presentation about RxJava, that should be given on [JEEConf 2016](http://jeeconf.com). It contains slides, presented code samples, and some useful links.

Slides for presentation are hosted on [Slideshare](http://www.slideshare.net/neposuda)
Demo application developed along presentation slides: [Twitter Breeze](http://www.slideshare.net/neposuda)

Good starting points:
- [Reactive manifesto](http://www.reactivemanifesto.org)
- [Collection of open source projects & documentation, including Netflix JavaRx](http://reactivex.io)

Recommended tutorials and courses:
- [Intro To RxJava](https://github.com/Froussios/Intro-To-RxJava)
- [Coursera: Principles of Reactive Programming](https://www.coursera.org/course/reactive)

Reactive programming for Scala:
- [RxScala - RxJava binding](http://reactivex.io/rxscala/)
- [Scientific paper: Ingo Maier Martin Odersky: Deprecating the Observer Pattern with Scala.React](http://infoscience.epfl.ch/record/176887/files/DeprecatingObservers2012.pdf)
- [Scala.Rx: experimental feature of Scala SDK](https://github.com/lihaoyi/scala.rx)

Reactive programming for other platforms:
- [.NET F#: Functional Reactive Programming](https://fsharpforfunandprofit.com/posts/concurrency-reactive/)

Recommended videos:
- [Artur Glier: Learn you some Rx for the greater good](https://www.youtube.com/watch?v=BujWQSjtplc)
- [Ross Hambrick: RxJava and Retrolambda Making Android | Devnexus 2015](https://www.youtube.com/watch?v=vRl3u1I9v2M)
- [Li Haoyi: Fun Functional-Reactive Programming with Scala.Rx](https://www.youtube.com/watch?v=i9mPUU1gu_8)
- [Ben Christensen: Functional Reactive Programming with RxJava, Netflix](https://www.youtube.com/watch?v=_t06LRX0DV0)
- [Erik Meijer: A Playful Introduction to Rx](https://www.youtube.com/watch?v=WKore-AkisY)
- [Chris Richardson: Futures and Rx Observables: powerful abstractions for consuming web services asynchronously](https://www.youtube.com/watch?v=aZkwIA4k2xU)
- [Roland Kuhn: Reactive Design Patterns](https://www.youtube.com/watch?v=nSfXcSWq0ug)



Key concepts to dive in:
- Backpressure (!!)
- Low level threading control (!!)

Articles:
- Schedulers (RU) (https://habrahabr.ru/company/rambler-co/blog/280388/)

Good Presentations:
- http://www.slideshare.net/allegrotech/rxjava-introduction-context
- http://www.slideshare.net/rickbwarren/building-scalable-stateless-applications-with-rx-java
- http://www.slideshare.net/Couchbase/reactive-programmingrxjavaefficientdata-benchristensenmichaelnitschinger
- http://www.slideshare.net/SpringCentral/practical-rxjava
- (Java 8 Streaming API vs RxJava) http://www.slideshare.net/jpaumard/java-8-stream-api-and-rxjava-comparison

Notes:
- This blog post originally used the term "functional reactive programming" or FRP. This term was used in error.
    RxJava does not implement "continuous time" which is a requirement for FRP. (http://techblog.netflix.com/2013/02/rxjava-netflix-api.html)

Code samples for Demo:
- https://github.com/twitter/hbc - Twitter Streaming client

Other interesting links:
- Look at: https://github.com/sqshq/PiggyMetrics (Spring based Microservice solution)
- Railway Oriented Programming (does it apply to Reactive pattern?)


TODO: Port slides here, with code samples!
