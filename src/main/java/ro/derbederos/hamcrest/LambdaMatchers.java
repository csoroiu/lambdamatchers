package ro.derbederos.hamcrest;

import java.util.function.Function;
import java.util.stream.BaseStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.hamcrest.Matcher;

import lombok.experimental.UtilityClass;
import static org.hamcrest.Matchers.*;

@UtilityClass
public class LambdaMatchers {

    private static <S extends BaseStream<T, S>, T> Function<BaseStream<T, S>, Iterable<T>> streamToIterable() {
        return stream -> stream::iterator;
    }

    public static <T, U> Matcher<T> map(Function<T, U> mapper, Matcher<? super U> matcher) {
        return FunctionMatcher.mappedWith(mapper, matcher);
    }

    public static <T, U> Matcher<Iterable<? extends T>> mapIterable(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(iter -> StreamSupport.stream(iter.spliterator(), false).map(mapper)::iterator, matcher);
    }

    public static <T, U> Matcher<T[]> mapArray(Function<T, U> mapper, Matcher<Iterable<? super U>> matcher) {
        return map(array -> Stream.of(array).map(mapper)::iterator, matcher);
    }

    public static <T, U> Matcher<Stream<? extends T>> mapStream(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(stream -> stream.map(mapper)::iterator, matcher);
    }

    public static <T, S extends BaseStream<T, S>> Matcher<BaseStream<T, S>> emptyStream() {
        return map(streamToIterable(), emptyIterable());
    }
}
