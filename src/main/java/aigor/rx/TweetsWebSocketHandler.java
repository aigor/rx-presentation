package aigor.rx;

import aigor.rx.dto.KeyWordsRequest;
import aigor.rx.dto.TweetStatisticsEvent;
import aigor.rx.twitter.ProblemSolutions;
import aigor.rx.twitter.TwitterClient;
import aigor.rx.twitter.TwitterStreamClient;
import aigor.rx.twitter.dto.Tweet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.stream.Streams;
import ratpack.websocket.WebSocket;
import ratpack.websocket.WebSocketClose;
import ratpack.websocket.WebSocketHandler;
import ratpack.websocket.WebSocketMessage;
import ratpack.websocket.internal.WebsocketBroadcastSubscriber;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import java.nio.CharBuffer;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Handle WebSocket communication
 * Created by aigor on 13.10.16.
 *
 * TODO: Try broadcast in order to find out how long websocket lives
 */
class TweetsWebSocketHandler implements WebSocketHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(TweetsWebSocketHandler.class.getSimpleName());

    private final Context context;
    private final PublishSubject<String> outgoingStream;

    private WebSocket webSocket;
    private Subscription subscription;
    private MessageMapper messageMapper;
    private TwitterClientFactory twitterClientFactory;

    private PublishSubject<Tweet> hostTweetStream;
    private Subscription tweetStreamSubscription;
    private TwitterClient twitterClient;
    private TwitterStreamClient twitterStreamClient;
    private long startTime;

    public TweetsWebSocketHandler(Context context, TwitterClientFactory twitterClientFactory) {
        this.context = context;
        this.outgoingStream = PublishSubject.create();
        this.messageMapper = new MessageMapper();
        this.twitterClientFactory = twitterClientFactory;
    }

    @Override
    public String onOpen(WebSocket webSocket) throws Exception {
        this.webSocket = webSocket;
        this.startTime = System.currentTimeMillis();

        this.twitterClient = twitterClientFactory.twitterClient(true);
        this.twitterStreamClient = twitterClientFactory.twitterStreamClient();

        WebsocketBroadcastSubscriber subscriber = new WebsocketBroadcastSubscriber(webSocket);
        Streams
                .bindExec(byteBufferStream(RxReactiveStreams.toPublisher(outgoingStream), context.get(ByteBufAllocator.class)))
                .subscribe(subscriber);
        log.info("WebSocket opened for Web client");
        return "";
    }

    @Override
    public void onMessage(WebSocketMessage<String> frame) throws Exception {
        String requestMessage = frame.getText();
        log.info("WebSocket received request message: " + requestMessage);
        Optional<KeyWordsRequest> keyWordsRequest = messageMapper.toKeyWordsRequest(requestMessage);
        if (!keyWordsRequest.isPresent()){
            log.warn("Received invalid message that can not be parsed: " + requestMessage);
        }

        keyWordsRequest.ifPresent(request -> {
            cancelSubscriptionIfRequired();

            // TODO: Cancel on unsubscribe
            PublishSubject<Tweet> hostTweetStream = PublishSubject.create();
            tweetStreamSubscription = twitterStreamClient
                    .getStream(getKeyWords(request))
                    .subscribeOn(Schedulers.io())
                    .doOnUnsubscribe(() -> {
                        log.info("Stream for [" + request.q + "] finished due to subscription cancellation");
                    })
                    .subscribe(hostTweetStream);

            Observable<String> mappedTweetStream =
                    hostTweetStream
                    .map(t -> {
                        log.info("Processing tweet [" + t + "]");
                        return messageMapper.toJson(t);
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get);

            Observable<String> tweetsCount =
                    hostTweetStream
                    .subscribeOn(Schedulers.io())
                    .window(10, TimeUnit.SECONDS)
                    .flatMap(window -> window.subscribeOn(Schedulers.io()).count())
                    .doOnEach(c -> { log.info("Received tweets during last 10 seconds: " + c.getValue() );})
                    .map(TweetStatisticsEvent::new)
                    .map(c -> messageMapper.toJson(c))
                    .filter(Optional::isPresent)
                    .map(Optional::get);

            Observable<Tweet> tweetsFromMorePopularUsers = hostTweetStream
                    .scan((u1, u2) -> u1.author_followers > u2.author_followers ? u1 : u2)
                    .distinctUntilChanged();

            Observable<String> newMostPopularAuthorStream = tweetsFromMorePopularUsers
                    .flatMap(t -> new ProblemSolutions().getUserProfileAndLatestPopularTweet(twitterClient, t.author))
                    .subscribeOn(Schedulers.io())
                    .doOnEach(c -> {
                        log.info("New most popular author detected: " + c.getValue());
                    })
                    .map(c -> messageMapper.toJson(c))
                    .filter(Optional::isPresent)
                    .map(Optional::get);


            CompositeSubscription compositeSubscription = new CompositeSubscription();
            compositeSubscription.add(mappedTweetStream.subscribe(outgoingStream));
            compositeSubscription.add(tweetsCount.subscribe(outgoingStream));
            compositeSubscription.add(newMostPopularAuthorStream.subscribe(outgoingStream));

            subscription = compositeSubscription;
        });
    }

    private String[] getKeyWords(KeyWordsRequest request) {
        return request.q.trim().split("\\s*,\\s*");
    }

    private void cancelSubscriptionIfRequired() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (hostTweetStream != null){
            hostTweetStream.onCompleted();
        }
        if (tweetStreamSubscription != null && !tweetStreamSubscription.isUnsubscribed()){
            tweetStreamSubscription.unsubscribe();
        }
    }

    @Override
    public void onClose(WebSocketClose<String> close) throws Exception {
        cancelSubscriptionIfRequired();
        log.info("WebSocket closed for [" + close.getOpenResult()
                + "], fromServer: " + close.isFromServer() + ", fromClient: " + close.isFromClient()
                + ", socket lived for " + (System.currentTimeMillis() - this.startTime) + " ms.");
    }

    private static Publisher<ByteBuf> byteBufferStream(Publisher<String> broadcaster, ByteBufAllocator bufferAllocator) {
        return Streams.map(broadcaster, s ->
                ByteBufUtil.encodeString(bufferAllocator, CharBuffer.wrap(s), CharsetUtil.UTF_8)
        );
    }
}
