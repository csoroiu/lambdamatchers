package ro.derbederos.hamcrest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.hamcrest.Matcher;

import lombok.experimental.UtilityClass;
import static org.hamcrest.Matchers.*;

@UtilityClass
public class LambdaMatchers {

    private static <S extends BaseStream<T, S>, T> Function<BaseStream<T, S>, Iterable<T>> streamToIterable() {
        return stream -> {
            List<T> target = new ArrayList<>();
            stream.iterator().forEachRemaining(target::add);
            return target;
        };
    }

    public static <T, U> Matcher<T> map(Function<T, U> mapper, Matcher<? super U> matcher) {
        return FunctionMatcher.map(mapper, matcher);
    }

    public static <T, U> Matcher<Iterable<? extends T>> mapIterable(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(iter -> StreamSupport.stream(iter.spliterator(), false).map(mapper).collect(Collectors.toList()),
                matcher);
    }

    public static <T, U> Matcher<T[]> mapArray(Function<T, U> mapper, Matcher<Iterable<? super U>> matcher) {
        return map(array -> Stream.of(array).map(mapper).collect(Collectors.toList()), matcher);
    }

    public static <T, U> Matcher<Stream<? extends T>> mapStream(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(stream -> stream.map(mapper).collect(Collectors.toList()), matcher);
    }

    public static <T, S extends BaseStream<T, S>> Matcher<BaseStream<T, S>> emptyStream() {
        return map(streamToIterable(), emptyIterable());
    }
}
