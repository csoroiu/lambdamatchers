package ro.derbederos.hamcrest;

import java.util.function.Function;
import java.util.stream.BaseStream;

import org.hamcrest.Matcher;

import lombok.experimental.UtilityClass;
import static org.hamcrest.Matchers.*;

@UtilityClass
public class LambdaMatchers {

    private static <S extends BaseStream<T, S>, T> Function<BaseStream<T, S>, Iterable<T>> streamToIterable() {
        return stream -> stream::iterator;
    }

    public static <T, U> Matcher<T> mappedItem(Function<T, U> mapper, Matcher<? super U> matcher) {
        return FunctionMatcher.mappedWith(mapper, matcher);
    }

    @Deprecated
    public static <T, U> Matcher<Iterable<? extends T>> mappedIterable(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return IterableMappingMatcher.mappedWith(mapper, matcher);
    }

    @Deprecated
    public static <T, U> Matcher<T[]> mappedArray(Function<T, U> mapper, Matcher<Iterable<? super U>> matcher) {
        return ArrayMappingMatcher.arrayMappedWith(mapper, matcher);
    }

    public static <T, S extends BaseStream<T, S>> Matcher<BaseStream<T, S>> emptyStream() {
        return mappedItem(streamToIterable(), emptyIterable());
    }

}
