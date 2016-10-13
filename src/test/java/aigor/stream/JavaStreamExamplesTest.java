package aigor.stream;

import org.junit.Test;
import rx.schedulers.Schedulers;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.function.IntUnaryOperator.identity;

/**
 * Class that holds code snippets used in presentation slides (Java 8 Stream API)
 * Created by aigor on 07.10.16.
 */
public class JavaStreamExamplesTest {

    // -----------------------------------------------------------------------------------------------------------------
    // Static Employee class to represent processing with Streams
    // -----------------------------------------------------------------------------------------------------------------

    static class Employee {
        private int age;

        public Employee(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(new Employee(23), new Employee(34), new Employee(34), new Employee(45), new Employee(19));
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Old school data processing vs Stream API for data processing
    // -----------------------------------------------------------------------------------------------------------------

    @Test public void preJava8DataProcessing(){
        // Data creation duplicated in order to be easily copied to slides
        List<Employee> employees = getEmployees();

        Map<Integer, List<Employee>> ageDistribution = new HashMap<>();
        for (Employee employee : employees) {
            if (employee.getAge() > 25){
                List<Employee> thisAge = ageDistribution.get(employee.getAge());
                if (thisAge != null){
                    thisAge.add(employee);
                } else{
                    List<Employee> createThisAge = new ArrayList<>();
                    createThisAge.add(employee);
                    ageDistribution.put(employee.getAge(), createThisAge);
                }
            }
        }
        System.out.println(ageDistribution);

    }

    @Test public void streamCollectors(){
        List<Employee> employees = getEmployees();

        Map<Integer, List<Employee>> ageDistribution =
                employees.stream()
                        .filter(e -> e.getAge() > 25)
                        .collect(Collectors.groupingBy(Employee::getAge));
        System.out.println(ageDistribution);
    }

    @Test public void streamCollectorsAlternative(){
        List<Employee> employees = getEmployees();

        Map<Integer, Long> ageDistribution =
        employees.stream()
                .filter(e -> e.getAge() > 25)
                .collect(Collectors.groupingBy(
                        Employee::getAge,
                        Collectors.counting()
                ));
        System.out.println(ageDistribution);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Stream operators
    // -----------------------------------------------------------------------------------------------------------------

    @Test public void simpleStreamOperators(){
        Stream.of("first", "second", "third", "4")
                .filter(s -> s.length() < 6)
                .map(String::toUpperCase)
                .forEach(System.out::println);

    }

    // -----------------------------------------------------------------------------------------------------------------
    // Sequence generation - different ways
    // -----------------------------------------------------------------------------------------------------------------

    @Test public void simpleStreamSources(){
        Stream<String> stream1  = Arrays.asList("A", "B", "C").stream();
        Stream<String> stream2  = Stream.of("Q", "P", "R");
        IntStream chars         = "some text".chars();
        Stream<String> words    = Pattern.compile(" ").splitAsStream("some other text");
    }

    @Test public void generateAsStreamSources(){
        Stream.generate(() -> UUID.randomUUID().toString())
                .limit(10)
                .forEach(System.out::println);
    }

    static class RandomSpliterator implements Spliterator<String> {
        public boolean tryAdvance(Consumer<? super String> action) {
            action.accept(UUID.randomUUID().toString());
            return true;
        }

        public Spliterator<String> trySplit() {
            return null;
        }

        public long estimateSize() {
            return Long.MAX_VALUE;
        };

        public int characteristics() {
            return NONNULL | DISTINCT;
        }
    }


    @Test public void simpleSpliteratorExample(){
        Stream<String> stream = StreamSupport.stream(new RandomSpliterator(), false);
        stream.limit(2).forEach(System.out::println);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Sequences to generate: 1, 2, 2, 3, 3, 3, 4, 4, 4, 4 ...
    // -----------------------------------------------------------------------------------------------------------------

    private List<Integer> generateSequenceOldStyle(int maxValue){
        List<Integer> data = new ArrayList<>();
        for (int i = 1; i<= maxValue; i++){
            for (int j = 1; j <= i ; j++){
                data.add(i);
            }
        }
        return data;
    }

    @Test public void oneTwoThreeSequenceOldStyle(){
        generateSequenceOldStyle(4).stream().forEach(System.out::println);
    }

    @Test public void oneTwoThreeSequenceNewStyle(){
        IntStream sequence =
                IntStream.rangeClosed(1, 50)
                        .flatMap(i ->
                                IntStream.iterate(i, identity()).limit(i)
                        );
        sequence.forEach(System.out::println);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Advanced spliterator - double every incoming stream item
    // -----------------------------------------------------------------------------------------------------------------

    interface ValueWrapper<T> {
        T get();

        default ValueWrapper set(T v){
            return () -> v;
        }
    }

    static class DoublerSpliterator<T> implements Spliterator<T> {
        private final Spliterator<T> originalSpliterator;

        private ValueWrapper<T> current = () -> null;
        private ValueWrapper<Boolean> hasElement = () -> true;
        private ValueWrapper<Boolean> toRead = () -> true;

        public DoublerSpliterator(Spliterator<T> originalSpliterator) {
            this.originalSpliterator = originalSpliterator;
        }

        public boolean tryAdvance(Consumer<? super T> action) {
            if (toRead.get()) {
                hasElement = hasElement.set(originalSpliterator.tryAdvance((T value) -> current = current.set(value)));
                if (hasElement.get()) {
                    action.accept(current.get());
                }
                toRead = toRead.set(false);
            } else {
                action.accept(current.get());
                toRead = toRead.set(true);
            }
            return hasElement.get();
        }

        public Spliterator<T> trySplit() {
            return null;
        }

        public long estimateSize() {
            return originalSpliterator.estimateSize() * 2;
        };

        public int characteristics() {
            return originalSpliterator.characteristics();
        }
    }

    @Test public void spliteratorExample(){
        Spliterator<String> spliterator = Stream.generate(() -> UUID.randomUUID().toString()).limit(3).spliterator();
        Stream<String> stream = StreamSupport.stream(new DoublerSpliterator<>(spliterator), false);
        stream.limit(7).forEach(System.out::println);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Async computation with Stream API (simple, parallel, custom ForkJoinPool) & RxJava
    // -----------------------------------------------------------------------------------------------------------------
    private List<String> generateRequestData() {
        return Stream.generate(() -> UUID.randomUUID().toString())
                .limit(50)
                .collect(Collectors.toList());
    }

    private String doRequest(String request){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[" + Thread.currentThread().getName() + "] processing request: " + request);
        return "response: " + request;
    }

    private void stopwatch(Runnable action){
        long start = System.currentTimeMillis();
        action.run();
        System.out.println("Execution took: " + (System.currentTimeMillis() - start) + " ms.");
    }

    @Test public void asyncComputationWithStreamAPI(){
        stopwatch(() ->
            generateRequestData().stream()
                    .map(req -> doRequest(req))
                    .collect(Collectors.toList())
        );
    }

    @Test public void asyncComputationWithParallelStreamAPI(){
        stopwatch(() ->
            generateRequestData().stream()
                .parallel()
                .map(req -> doRequest(req))
                .collect(Collectors.toList())
        );
    }

    @Test public void asyncComputationWithForkJoinPool(){
        ForkJoinPool forkJoinPool = new ForkJoinPool(80);
        stopwatch(() -> {
                    try {
                        forkJoinPool.submit(() ->
                                generateRequestData().stream()
                                        .parallel()
                                        .map(req -> doRequest(req))
                                        .collect(Collectors.toList())
                        ).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Test public void asyncComputationWithRxJava(){
        stopwatch(() ->
            rx.Observable.from(generateRequestData())
                .flatMap(req ->
                        rx.Observable.just(req)
                        .subscribeOn(Schedulers.io())
                        .map(actualReq -> doRequest(actualReq)))
                .toList()
                .toBlocking()
                .first()
        );
    }
}