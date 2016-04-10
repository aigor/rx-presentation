# RxJava Applied: Consise Examples where It Shines
- Language: Ukrainian
- My aim: Show how good RxJava & FRP are for software integration
- Attention attraction: Solve complex integration scenario in 20 lines of code!
- What I want: I want listeners to add FRP & RxJava lo list of their every-day instruments 

## Presentation plan
1. Introduction (2 min)
	* Presentation name and info about speaker: seaker (name, company, background) & talk topic (20 sec)
	* Notice: We are tight on time so all Q&A would be at the end of presentation, please do not interrupt without serious reason (20 sec)
	* It is important for software: Integration, Workflows (synch & asynch)
	* Options to deal with complexity of integration (slide with alternative's logos)
	* One more option: RxJava - our applicant for today (10 sec)
	* RxJava history chart (Aim: it is already mature library, 30 sec)
2. Main part: Problem solving (10 min)
	* Warm-up problem: resolve user status, photo, and last 5 tweets in parallel (3 min)
		- Problem requirements (30 sec)
		- Possible solution strategy on diagram (30 sec)
		- Notice: Assumptions about class that is used for actual HTTP requests (15 sec)
		- Code for RxJava solution (up to 7 lines, speaker describes what each line of code does) (1 min)
		- Code benefits: We can chose how many threads would be involved (single thread or in parallel, maybe show running logs) (30 sec)
	* Pretty complicated problem: for straem of users, lookup info, handle errors, log, geather statistics, apply backpressure, create events (7 min)
		- Problem requirements in details (1 min)
		- Solution strategy (diagram) (1 min)
		- Try to reuse previous problem (15 sec)
		- Code for RxJava solution (1 slide, up to 20 lines of code, could be not very readable, but point in showing whole solution on one slide) (30 sec)
		- Description of parts of code (2-4 slides with line by line explenations) (2 min)
		- Marble diagram for stream of data (if it isn't very complicated, candidate to be ommited) (15 sec)
		- Benefits: Threading options, retry strategies, backpreasure from the box, ease of event generation (30 sec)
3. Conclusion: it is better to finish with benefits (2 min)
	* RxJava pitfalls (1 slide, TOOD: Review this):
		- A lot of small building blocks (observable transformations), it requires some learning
		- Requires to understand threadpools and undelining magic, take care about back pressure (it is not silver bullet)
	* RxJava strength (2 slides, TODO: Review this):
		- It is function, It is reactive
		- Good for complecated integration scenarios
		- Allows to control execution threads
		- Easy to compose workflows
		- Easy to test
		- Easy to integrate into current solutions
		- Has great success in Android development
		- Good documentation
4. Q&A (TODO: would it be time for Q&A on lightning talk?) (1 min)
	* TODO: Add list of most likely questions

## Presntation notices
- We are tight on time so all Q&A would be at the end of presentation, please do not interrupt without serious reason
- Ommit all non vital details, be concise
- I can only name alternatives for RxJava:
	- Manual managment of synchronization primitives (locks, queues, barriers, thread executors)
	- Callbacks (TODO: look for libararies)
	- CompletableFuture (Java 8) & Scala futures
	- Java 8 Iterables & parallel execution (hard to handle errors & compose different operations)
	- EIP & Apache Camel library (TODO: Look at current status)
	- Java Promises & JDeffer (TODO: Look into library)
	- Actor model & Akka (Erlang, GPars, etc)
	- Scala FRP implentation: scala.rx
	- And a lot of other options
- I do not want to compare RxJava with other libraries, it would be too hard
- I want to show strong sides of RxJava, how lightweigh it is, and how easy to start using it
- All used code should be accessible on GitHub repo
- Main part slides should contain link to GitHub repo

## Possible questins
- Performance comparison - very rough, it is not JMH (TODO: Add solutions and test suite for comparison)
- Ability to integrate with Java NIO, Netty, asynch servlets (TODO: Add code samples)

## TODO for presentation
- Find good preentation template (including pretty font)
- Find callback based library options for Java (I saw some for Android)
- Look into JDeffer
- Look at Apache Camel current status
- Review RxJava strangs & pitfalls with community (it shouldn't by only my opinion)
- Try to solve problem using other approaches and libraries (at least Camel, Akka, GPars, Scala, JDeffer, CompletableFuture)
- Add some performance testing suite for rough comparison
- Investigate in details ability to integrate with Java NIO, Netty, asynch servlets (possible Q&A)
- Add list of most likely questions (and answers for them)