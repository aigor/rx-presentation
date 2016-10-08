package aigor.stream;

import java.util.function.Consumer;

/**
 * Created by aigor on 07.10.16.
 */
public interface TSpliterator<T> {

    boolean tryAdvance(Consumer<? super T> action);

    TSpliterator<T> trySplit();

    long estimateSize();

    int characteristics();
}



