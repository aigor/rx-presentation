package aigor.stream;

import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.lang.Thread.currentThread;

/**
 * Created by aigor on 08.10.16.
 */
public class RxStreamTest {
    @Test public void filterTest(){
        Observable.from(Arrays.asList(1, 2, 5, 7, 8, 12, 3, 6, 7, 8))
                .filter(i -> (i > 3 && i < 8))
                .forEach(System.out::println);
    }

    @Test public void createTest(){
        Observable<Integer> empty = Observable.empty();
        Observable<Integer> never = Observable.never();
        Observable<Integer> error = Observable.error(new RuntimeException("Error"));
    }

    @Test public void timeSeries() throws InterruptedException {
        Observable<Long> timer = Observable.timer(2, TimeUnit.SECONDS);
        Observable<Long> interval = Observable.interval(1, TimeUnit.SECONDS);

        timer.forEach(i -> System.out.println(currentThread().getName() + " - " + i));
        interval.forEach(i -> System.out.println(currentThread().getName() + " - " + i));

        Thread.sleep(2000);
    }

    @Test public void schedulersSeries() throws InterruptedException {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .map( i -> "Elapsed time: " + i + " seconds")
                .take(3)
                .observeOn(Schedulers.io())
                .forEach(s -> System.out.println(currentThread().getName() + ": " + s));

        Thread.sleep(5000);

//        Observable.from("one", "two", "three", "four", "five")
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(/* an Observer */);
    }

    @Test public void merge() throws InterruptedException {
        Observable<Integer> odds = Observable.just(1, 3, 5).subscribeOn(Schedulers.io());
        Observable<Integer> evens = Observable.just(2, 4, 6);

        Observable.merge(odds, evens)
                .subscribe(
                        System.out::println,
                        System.err::println,
                        () -> System.out.println("Finished"));
        Thread.sleep(10);
    }

    @Test public void zip() throws InterruptedException {
        Observable<String> odds = Observable.just("A", "B", "C", "D");
        Observable<Integer> evens = Observable.just(2, 4, 6);

        Observable.zip(odds, evens, (a, b) -> a + "-" + b)
                .subscribe(
                        System.out::println,
                        System.err::println,
                        () -> System.out.println("Finished"));
    }

    @Test public void iterator() {
//        Iterator<T> iterator = ...;
//        Observable<T> observable = Observable.from(() -> iterator);
    }

    public static<T> Iterator<T> of (Observable<? extends T> observable){
//        class Adapter implements Iterator<T>, Consumer<T> {
//            ...
//        }
//
//        return new Adapter();
        return null;
    }
}
