package aigor.stream;

import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.function.IntUnaryOperator.identity;

/**
 * Created by aigor on 07.10.16.
 */
public class JavaStreamExamplesTest {
    @Test public void simpleStream(){
        Stream<String> stream = Stream.of("first", "second", "third", "4");
        stream.filter(s -> s.length() < 6)
              .map(String::toUpperCase)
              .forEach(System.out::println);

    }

    static class Employee {
        private int age;

        public Employee(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }
    }

    @Test public void streamCollectors(){
        List<Employee> employees = Arrays.asList(new Employee(23), new Employee(34), new Employee(34), new Employee(45), new Employee(19));

        Map<Integer, Long> ageDistribution =
        employees.stream()
                .filter(e -> e.getAge() > 25)
                .collect(Collectors.groupingBy(
                        Employee::getAge,
                        Collectors.counting()
                ));
        System.out.println(ageDistribution);
    }

    @Test public void streamCollectorsOld(){
        List<Employee> employees = Arrays.asList(new Employee(23), new Employee(34), new Employee(34), new Employee(45), new Employee(19));

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

    @Test public void streamCollectorsNew(){
        List<Employee> employees = Arrays.asList(new Employee(23), new Employee(34), new Employee(34), new Employee(45), new Employee(19));

        Map<Integer, List<Employee>> ageDistribution =
                employees.stream()
                        .filter(e -> e.getAge() > 25)
                        .collect(Collectors.groupingBy(Employee::getAge));
        System.out.println(ageDistribution);
    }



    @Test public void streamSources(){
        Stream<String> stream1 = Arrays.asList("A", "B", "C").stream();
        Stream<String> stream2 = Stream.of("Q", "P", "R");
        IntStream chars = "some text".chars();
        Stream<String> words = Pattern.compile(" ").splitAsStream("some other text");
    }

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
        Spliterator<String> spliterator = Stream.generate(() -> UUID.randomUUID().toString()).limit(1).spliterator();
        Stream<String> stream = StreamSupport.stream(new DoublerSpliterator<>(spliterator), false);
        stream.limit(3).forEach(System.out::println);
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

    @Test public void oneTwoThreesequence(){
        IntStream sequence =
            IntStream.rangeClosed(1, 50)
                .flatMap(i ->
                        IntStream.iterate(i, identity()).limit(i)
                );
        sequence.forEach(System.out::println);
    }

    List<Integer> generateSequence(int maxValue){
        List<Integer> data = new ArrayList<>();
        for (int i = 1; i<= maxValue; i++){
            for (int j = 1; j <= i ; j++){
                data.add(i);
            }
        }
        return data;
    }

    @Test public void oneTwoThreeSequenceOldStyle(){
        generateSequence(4).stream().forEach(System.out::println);
    }



}