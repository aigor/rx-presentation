# RxJava Applied: Concise Examples where It Shines
- Language: Ukrainian
- My aim: Show how good RxJava & FRP are for software integration
- Attention attraction: Solve complex integration scenario in 20 lines of code!
- What I want: I want listeners to add FRP & RxJava to list of everyday instruments 

## Presentation plan
* __Introduction (2 min)__
	* Presentation name and info about the speaker: speaker name, company, background & talk topic (20 sec)
	* Notice: We are tight on time, so all Q&A would be at the end of the presentation, please do not interrupt without serious reason (20 sec)
	* It is important for software: Integration, Data Workflows (synch & asynch)
	* Options to deal with complexity of integration (slide with alternative's logos)
	* One more option: RxJava - our applicant for today (10 sec)
	* RxJava history chart (Aim: it is already mature library, 20 sec)
* __Main part: Problem solving (10 min)__
	* Warm-up problem: resolve user status, photo, and last 5 tweets in parallel (3 min)
		- Problem requirements (30 sec)
		- Possible solution strategy on the diagram (30 sec)
		- Notice: Assumptions about the class used for actual HTTP requests (15 sec)
		- Code for RxJava solution (up to 7 lines, the speaker describes what each line of code does) (1 min)
		- Code benefits: We can choose how many threads would be involved (single thread or in parallel, maybe show running logs) (30 sec)
	* Pretty complicated problem: for stream of users, lookup info, handle errors, log, gather statistics, apply backpressure, create events (7 min)
		- Problem requirements in details (1 min)
		- Solution strategy (diagram) (1 min)
		- Try to reuse previous problem (15 sec)
		- Code for RxJava solution (1 slide, up to 20 lines of code, could be not very readable, but point in showing whole solution on one slide) (30 sec)
		- Description of parts of code (2-4 slides with line by line explanations) (2 min)
		- Marble diagram for streams of data (if it isn't very complicated, candidate to be omitted) (15 sec)
		- Benefits: Threading options, retry strategies, backpreasure from the box, ease of event generation (30 sec)
* __Conclusion: it is better to finish with benefits (1 min)__
	* RxJava pitfalls (1 slide, TODO: Review this):
		- A lot of small building blocks (observable transformations), it requires some learning
		- Requires to understand threadpools and underlying magic, take care about back pressure (it is not silver bullet)
	* RxJava strength (2 slides, TODO: Review this):
		- It is function, It is reactive
		- Good for complicated integration scenarios
		- Allows to control execution threads
		- Easy to compose workflows
		- Easy to test
		- Easy to integrate into current solutions
		- Has great success in Android development
		- Good documentation
* __Q&A (2 min)__
	* TODO: Add list of most likely questions

## Presentation notices
- We are tight on time, so all Q&A would be at the end of the presentation, please do not interrupt without serious reason
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
	- And some other options
- I do not want to compare RxJava with other libraries, it would be too hard
- I want to show strong sides of RxJava, how lightweight it is, and how easy to start using it
- All used code should be accessible on GitHub repo
- Main part slides should contain a link to GitHub repo

## Possible questions
- Performance comparison - very rough, it is not JMH (TODO: Add solutions and test suite for comparison)
- Ability to integrate with Java NIO, Netty, asynchronous servlets (TODO: Add code samples)

## TODO for presentation
- Find good presentation template (including pretty font)
- Find callback based library options for Java (I saw some for Android)
- Look into JDeferred
- Look at Apache Camel current status
- Review RxJava strength & pitfalls with community (it shouldn't by only my opinion)
- Try to solve mentioned problems using other approaches and libraries (at least Camel, Akka, GPars, Scala, JDeferred, CompletableFuture)
- Add some performance testing suite for a rough comparison
- Investigate in details ability to integrate with Java NIO, Netty, asynchronous servlets (possible Q&A)
- Add a list of the most likely questions (and answers for them)