package aigor.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.groovy.template.TextTemplateModule;
import ratpack.guice.Guice;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;
import ratpack.websocket.*;

import static ratpack.groovy.Groovy.groovyTemplate;

/**
 * Entry point for Application
 *
 * Created by aigor on 13.10.16.
 */
public class RatpackApplication {
    private static final Logger log = LoggerFactory.getLogger(RatpackApplication.class.getSimpleName());

    public static void main(String... args) throws Exception {
        log.info("TweetBuzz application starting...");

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
                            WebSockets.websocket(context, new TweetsWebSocketHandler(context, new TwitterClientFactory()));
                        })
                        .files(f -> f.dir("public"))
                )
        );
    }


}
