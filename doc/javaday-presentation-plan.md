# RxJava Applied: Concise Examples where It Shines
- Language: Ukrainian
- My aim: Show how good RxJava and Reactive Streams for some scenarios in software
- Attention attraction: Solve complex integration scenario in 20-50 lines of code!
- What I want: I want listeners to add RxJava and Reactive Streams to list of everyday instruments

## Other JavaDay presentations on topic:
- Reactive Microservices with Vert.x
- A practical RxJava example with Ratpack and Couchbase (Beginner talk, try not to intersect)

## Open questions
- Scope of presentation before mine (Rx+Ratpack+Couchbase), try not to overlap too much
- Demo: should I show actual code?
- Demo: will I have some demo?

## Plan that this presentation follows (timing: 50 minutes)
- __Introduction (3 min)__
  - Presentation name and info about the speaker: speaker name, company, background & talk topic (1 min sec)
  - QR code for presentation slides & code (1 min)
  - Notice: Question session will be at the end, let's start!
- __Theoretical part (15 minutes)__
	- Java 8 stream essentials revision (what it is and what it for) (2 min)
	- Reactive Streams: what the difference (2 min)
	- RxJava - short history, introduction to the library (1 min)
	- Introduce marble diagrams (1 min)
	- Basic concepts comparison (Java 8 vs RxJava) (4 min) (Refer José Paumard video)
	- Basic performance comparison (based on José Paumard's benchmarks + my own benchmarks) (2 min)
	- What scenarios RxJava good for (Android, Integration, API, async bridging) (1 min)
	- RxJava is not new concept, don't afraid it (very similar to: Cool Callbacks, Apache Camel, Completable Futures, Streams) (1 min)
  - Current alternatives: Vert.x, Ratpack, etc (1 min)
- __Practical part (15 minutes) [TODO: annotate this part based on JEEConf video]__
	- Let's solve some problem (formulate problem: twitter stream analysis) (2 min)
	- Propose solution strategy (high level & low level) (1 min 30 sec)
	- Process with RxJava code on slides (6 min 30 sec)
	- Include one case of stream testing (1 min)
  - Say about Backpressure with some details (1 min)
	- Show solution diagrams again (30 sec)
	- Show actual demo resource (2 min 30 sec)
- __Conclusions (4 min)__
	- RxJava pitfalls
	- RxJava strength
	- Last note: RxJava future (Java 9, RxJava 2.0)
- __Q&A session (10 min)__
	- Q&A slide with QR code for presentation slides & code
	- Thanks slide (15 sec)

## Presentation notices
- We will have Q&A at the end of the presentation, please do not interrupt without serious reason
- Omit all non vital details, be concise
- I can only name alternatives for RxJava:
	- Manual management of synchronization primitives (locks, queues, barriers, thread executors)
	- Callbacks (TODO: look for libraries)
	- CompletableFuture (Java 8) & Scala futures
	- Java 8 Iterables & parallel execution (hard to handle errors & compose different operations)
	- EIP & Apache Camel library (TODO: Look at current status)
	- Java Promises & JDefered (TODO: Look into library)
	- Actor model & Akka (Erlang, GPars, etc.)
	- Scala FRP implementation: scala.rx
	- Reactive Streams alternatives
- I do not want to compare RxJava with other libraries, it would be too hard (each library has its own "best fit" scenarios)
- I want to show strong sides of RxJava, how lightweight it is, and how easy to start using it

## Possible questions
- Performance comparison - reuse Paul's comparison
- Ability to integrate with Java NIO, Netty, Retpack, asynchronous servlets (TODO: Add code samples)

## TODO for presentation
- Find callback based library options for Java (I saw some for Android)
- Look into JDeferred
- Look at Apache Camel current status
- Review RxJava strength & pitfalls with community (it shouldn't by only my opinion)
- Try to solve mentioned problems using other approaches and libraries (at least Camel, Akka, GPars, Scala, JDeferred, CompletableFuture)
- Add some performance testing suite for a rough comparison (May use Paul's)
- Investigate in details ability to integrate with Java NIO, Netty, asynchronous servlets (possible Q&A)
- Add a list of the most likely questions (and answers for them)
