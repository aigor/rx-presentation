# rx-presentation
Reactive software development of JVM

Good starting points:
- http://www.reactivemanifesto.org/ (reactive manifesto)
- http://reactivex.io/ (Collection of open source projects & documentation, including Netflix JavaRx)
- https://www.coursera.org/course/reactive (Coursera Scala+Rx course)

Definitions:
- Functional Reactive Programming (in Scala) - FRP  /TODO: define/

Java libraries:
- RxJava (has ports to Groovy, Clojure, Scala, Kotlin)

Scala libraries:
- RxScala (http://reactivex.io/rxscala/) /TODO: investigate/
- Scientific paper: Ingo Maier Martin Odersky: Deprecating the Observer Pattern with Scala.React (http://infoscience.epfl.ch/record/176887/files/DeprecatingObservers2012.pdf)
- Scala.Rx (https://github.com/lihaoyi/scala.rx) /experimental feature of Scala SDK/

Other platforms:
- .NET F# (https://fsharpforfunandprofit.com/posts/concurrency-reactive/)

Key concepts to dive in:
- Backpressure (!!)
- Low level threading control (!!)
- Railway Oriented Programming (does it apply to Reactive pattern?)

Videos:
- (!! Learn you some Rx for the greater good) https://www.youtube.com/watch?v=BujWQSjtplc
- (! Devnexus 2015 - RxJava and Retrolambda Making Android - Ross Hambrick) https://www.youtube.com/watch?v=vRl3u1I9v2M
- (Li Haoyi: Fun Functional-Reactive Programming with Scala.Rx) https://www.youtube.com/watch?v=i9mPUU1gu_8
- (Ben Christensen: Functional Reactive Programming with RxJava, Netflix) https://www.youtube.com/watch?v=_t06LRX0DV0 (watched)
- (Erik Meijer: A Playful Introduction to Rx) https://www.youtube.com/watch?v=WKore-AkisY (watched)
- (Chris Richardson: Futures and Rx Observables: powerful abstractions for consuming web services asynchronously) https://www.youtube.com/watch?v=aZkwIA4k2xU
- (Reactive Design Patterns) https://www.youtube.com/watch?v=nSfXcSWq0ug

Articles:
- Schedulers (RU) (https://habrahabr.ru/company/rambler-co/blog/280388/)

Good Presentations:
- http://www.slideshare.net/allegrotech/rxjava-introduction-context
- http://www.slideshare.net/rickbwarren/building-scalable-stateless-applications-with-rx-java
- http://www.slideshare.net/Couchbase/reactive-programmingrxjavaefficientdata-benchristensenmichaelnitschinger
- http://www.slideshare.net/SpringCentral/practical-rxjava
- (Java 8 Streaming API vs RxJava) http://www.slideshare.net/jpaumard/java-8-stream-api-and-rxjava-comparison


TODO, FIND OUT:
- This blog post originally used the term "functional reactive programming" or FRP. This term was used in error. RxJava does not implement "continuous time" which is a requirement for FRP from previous literature. (http://techblog.netflix.com/2013/02/rxjava-netflix-api.html)
- Look at: https://github.com/sqshq/PiggyMetrics (Spring based Microservice solution)

Code samples:
- https://github.com/twitter/hbc - Twitter Streaming client