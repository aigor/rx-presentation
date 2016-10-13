package aigor.rx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.groovy.template.TextTemplateModule;
import ratpack.guice.Guice;
import ratpack.handling.Context;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;
import ratpack.stream.Streams;
import ratpack.websocket.*;
import ratpack.websocket.internal.WebsocketBroadcastSubscriber;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscription;
import rx.subjects.PublishSubject;

import java.nio.CharBuffer;
import java.util.concurrent.TimeUnit;

import static ratpack.groovy.Groovy.groovyTemplate;

/**
 * Entry point for Application
 *
 * Created by aigor on 13.10.16.
 */
public class RatpackApplication {
    private static final Logger log = LoggerFactory.getLogger(RatpackApplication.class.getSimpleName());

    // ENTRY POINT
    public static void main(String... args) throws Exception {
        RatpackServer.start(s -> s
                .serverConfig(c -> c
                        .baseDir(BaseDir.find())
                        .env())

                .registry(Guice.registry(b -> b
                        .module(TextTemplateModule.class, conf -> conf.setStaticallyCompile(true))))

                .handlers(chain -> chain
                        .get(ctx -> ctx.render(groovyTemplate("index.html")))
                        .get("hello", context -> {
                            context.render("Hello TwitterBuzz user!");
                        })
                        .get("ws", context -> {
                            WebSockets.websocket(context, new TweetsWebSocketHandler(context));
                        })
                        .files(f -> f.dir("public"))
                )
        );
    }

    private static Publisher<ByteBuf> byteBufferStream(Publisher<String> broadcaster, ByteBufAllocator bufferAllocator){
        return Streams.map(broadcaster, s ->
                ByteBufUtil.encodeString(bufferAllocator, CharBuffer.wrap(s), CharsetUtil.UTF_8)
        );
    }

    private static class TweetsWebSocketHandler implements WebSocketHandler<String> {
        private final Context context;
        private final PublishSubject<String> outgoingStream;

        private WebSocket webSocket;
        private Subscription subscription;

        public TweetsWebSocketHandler(Context context) {
            this.context = context;
            this.outgoingStream = PublishSubject.create();
        }

        @Override
        public String onOpen(WebSocket webSocket) throws Exception {
            this.webSocket = webSocket;
            WebsocketBroadcastSubscriber subscriber = new WebsocketBroadcastSubscriber(webSocket);
            Streams
                    .bindExec(byteBufferStream(RxReactiveStreams.toPublisher(outgoingStream), context.get(ByteBufAllocator.class)))
                    .subscribe(subscriber);
            log.info("WebSocket opened for Web client");
            return "";
        }

        @Override
        public void onMessage(WebSocketMessage<String> frame) throws Exception {
            String request = frame.getText();
            log.info("WebSocket message received: " + request);
            webSocket.send("Pong: " + request);

            if (subscription != null && !subscription.isUnsubscribed()){
                subscription.unsubscribe();
            }

            Observable<String> dataStream = Observable
                    .interval(1, TimeUnit.SECONDS)
                    .map(i -> { log.debug("Generating data for request [" + request + "]"); return request;})
                    .doOnUnsubscribe(() -> { log.info("Stream for [" + request + "] finished due to subscription cancellation" );});

            subscription = dataStream.subscribe(outgoingStream);
        }

        @Override
        public void onClose(WebSocketClose<String> close) throws Exception {
            log.info("WebSocket closed for Web client");

            if (subscription != null && !subscription.isUnsubscribed()){
                subscription.unsubscribe();
            }
        }
    }
}
