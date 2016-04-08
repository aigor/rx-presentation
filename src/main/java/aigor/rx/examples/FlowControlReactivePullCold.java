package aigor.rx.examples;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by aigor on 08.04.16.
 */
public class FlowControlReactivePullCold {

    public static void main(String[] args) {
        getData(1).observeOn(Schedulers.computation()).toBlocking().forEach(i -> System.out.println(i + ":" + Thread.currentThread().getId()));
    }

    /**
     * This is a simple example of an Observable Iterable using "reactive pull".
     */
    public static Observable<Integer> getData(int id) {
        // simulate a finite, cold data source
        final ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            data.add(i + id);
        }
        return fromIterable(data);
    }

    /**
     * A more performant but more complicated implementation can be seen at:
     * https://github.com/Netflix/RxJava/blob/master/rxjava-core/src/main/java/rx/internal/operators/OnSubscribeFromIterable.java
     * <p>
     * Real code should just use Observable.from(Iterable iter) instead of re-implementing this logic.
     * <p>
     * This is being shown as a simplified version to demonstrate how "reactive pull" data sources are implemented.
     */
    public static Observable<Integer> fromIterable(Iterable<Integer> it) {
        // return as Observable (real code would likely do IO of some kind)
        return Observable.create((Subscriber<? super Integer> s) -> {
            final Iterator<Integer> iter = it.iterator();
            final AtomicLong requested = new AtomicLong();
            s.setProducer((long request) -> {
                /*
                 * We add the request but only kick off work if at 0.
                 *
                 * This is done because over async boundaries `request(n)` can be called multiple times by
                 * another thread while this `Producer` is still emitting. We only want one thread ever emitting.
                 */
                if (requested.getAndAdd(request) == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    do {
                        if(s.isUnsubscribed()) {
                            return;
                        }
                        if (iter.hasNext()) {
                            s.onNext(iter.next());
                        } else {
                            s.onCompleted();
                        }
                    } while (requested.decrementAndGet() > 0);
                }
            });
        });
    }
}
