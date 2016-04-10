package aigor.rx;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.*;

/**
 * File with RxDemo
 */
public class RxDemo {

    private static ExecutorService rxExecutor = Executors.newWorkStealingPool(100);
    private static ExecutorService requestExecutor = Executors.newFixedThreadPool(50);

    public static void main(String[] args) throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        final int tasks = 100;
        CountDownLatch latch = new CountDownLatch(tasks);
        for (int i=0; i < tasks; i++) {
            requestExecutor.execute(() -> {
                Observable<Tile> searchTile = getSearchResults("search term")
                        .doOnSubscribe(() -> logTime("Search started", startTime))
                        .doOnCompleted(() -> logTime("Search completed", startTime));

                Observable<TileResponse> populatedTiles = searchTile.flatMap(t -> {
                    Observable<Reviews> reviews = getSellerReviews(t.getSellerId())
                            .doOnCompleted(() -> logTime("  getSellerReviews for [" + t.id + "] completed", startTime));
                    Observable<String> imageUrl = getProductImage(t.getProductId())
                            .doOnCompleted(() -> logTime("  getProductImage for [" + t.id + "] completed", startTime));

                    return Observable.zip(reviews, imageUrl, (r, u) -> new TileResponse(t, r, u))
                            .doOnCompleted(() -> logTime("  zip for [" + t.id + "] completed", startTime));
                });

                populatedTiles
                        .toList()
                        .doOnCompleted(() -> {
                            logTime("All Tiles Completed", startTime);
                            System.out.println("------------------------------------------------------------");
                            latch.countDown();
                        })
                        .toBlocking().single();
            });
        }
        latch.await();
        rxExecutor.shutdown();
        requestExecutor.shutdown();
    }

    private static Observable<Tile> getSearchResults(String string) {
        return mockClient(new Tile(1), new Tile(2), new Tile(3));
    }

    private static Observable<Reviews> getSellerReviews(int id) {
        return mockClient(new Reviews());
    }

    private static Observable<String> getProductImage(int id) {
        return mockClient("image_" + id);
    }

    private static void logTime(String message, long startTime) {
        System.out.println(String.format("[%4s ms] [T:%3s] %s", (System.currentTimeMillis() - startTime), Thread.currentThread().getId(), message));
    }

    private static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (Exception e) {}
    }

    private static <T> Observable<T> mockClient(T... ts) {
        return Observable.create((Subscriber<? super T> s) -> {
            // simulate latency
            sleep(700);
            for (T t : ts) {
                sleep(100);
                s.onNext(t);
            }
            s.onCompleted();
        }).subscribeOn(Schedulers.from(rxExecutor));
        // note the use of subscribeOn to make an otherwise synchronous Observable async
    }

    public static class TileResponse {
        public TileResponse(Tile t, Reviews r, String u) {
            // store the values
        }
    }

    public static class Tile {
        private final int id;

        public Tile(int i) {
            this.id = i;
        }

        public int getSellerId() {
            return id;
        }

        public int getProductId() {
            return id;
        }

    }

    public static class Reviews {

    }
}
