package ro.derbederos.hamcrest;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.emptyIterable;

public final class LambdaMatchers {

    private LambdaMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T, U> Matcher<T> map(Function<T, U> mapper, Matcher<? super U> matcher) {
        return FunctionMatcher.map(mapper, matcher);
    }

    public static <T, U> Matcher<Iterable<? extends T>> mapIterable(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(iter -> (Iterable<? super U>) StreamSupport.stream(iter.spliterator(), false).map(mapper).collect(Collectors.toList()), matcher);
    }

    public static <T, U> Matcher<T[]> mapArray(Function<T, U> mapper, Matcher<Iterable<? super U>> matcher) {
        return map(array -> (Iterable<? super U>) Stream.of(array).map(mapper).collect(Collectors.toList()), matcher);
    }

    public static <T, U> Matcher<Stream<? extends T>> mapStream(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(stream -> (Iterable<? super U>) stream.map(mapper).collect(Collectors.toList()), matcher);
    }

    public static <T> Matcher<Stream<? extends T>> toIterable(Matcher<Iterable<? super T>> matcher) {
        return map(stream -> (Iterable<? super T>) stream.collect(Collectors.toList()), matcher);
    }

    public static <T, S extends BaseStream<T, S>> Matcher<BaseStream<T, S>> emptyStream() {
        return map(baseStreamToIterable(), emptyIterable());
    }

    private static <S extends BaseStream<T, S>, T> Function<BaseStream<T, S>, Iterable<T>> baseStreamToIterable() {
        return stream -> {
            List<T> target = new ArrayList<>();
            stream.iterator().forEachRemaining(target::add);
            return target;
        };
    }
}
