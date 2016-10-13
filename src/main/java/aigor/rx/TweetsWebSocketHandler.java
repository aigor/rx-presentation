package aigor.rx;

import aigor.rx.dto.KeyWordsRequest;
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

import java.nio.CharBuffer;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Handle WebSocket communication
 * Created by aigor on 13.10.16.
 */
class TweetsWebSocketHandler implements WebSocketHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(TweetsWebSocketHandler.class.getSimpleName());

    private final Context context;
    private final PublishSubject<String> outgoingStream;

    private WebSocket webSocket;
    private Subscription subscription;
    private MessageMapper messageMapper;
    private TwitterClientFactory twitterClientFactory;

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
            Observable<String> mappedTweetStream = twitterStreamClient
                    .getStream(getKeyWords(request))
                    .subscribeOn(Schedulers.io())
                    .map(t -> {
                        log.info("Processing tweet [" + t + "]");
                        return messageMapper.toJson(t);
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .doOnUnsubscribe(() -> {
                        log.info("Stream for [" + request.q + "] finished due to subscription cancellation");
                    });

            subscription = mappedTweetStream.subscribe(outgoingStream);
        });
    }

    private String[] getKeyWords(KeyWordsRequest request) {
        return request.q.trim().split("\\s*,\\s*");
    }

    private void cancelSubscriptionIfRequired() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onClose(WebSocketClose<String> close) throws Exception {
        cancelSubscriptionIfRequired();
        log.info("Closing socket after: " + (System.currentTimeMillis() - this.startTime) + " ms.");
        log.info("WebSocket closed for Web client: " + close.getOpenResult()
                + ", isFromServer: " + close.isFromServer() + ", isFromClient: " + close.isFromClient());
    }

    private static Publisher<ByteBuf> byteBufferStream(Publisher<String> broadcaster, ByteBufAllocator bufferAllocator) {
        return Streams.map(broadcaster, s ->
                ByteBufUtil.encodeString(bufferAllocator, CharBuffer.wrap(s), CharsetUtil.UTF_8)
        );
    }
}
