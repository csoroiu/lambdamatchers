package ro.derbederos.hamcrest;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Iterator;
import java.util.function.Function;

@Deprecated
class ArrayMappingMatcher<T, U> extends FeatureMatcher<T[], Iterable<? super U>> {

    private final Function<T, U> mapper;

    private ArrayMappingMatcher(Function<T, U> mapper, Matcher<Iterable<? super U>> subMatcher) {
        super(subMatcher, "Feature", "");
        this.mapper = mapper;
    }

    @Override
    protected Iterable<U> featureValueOf(T[] actual) {
        return new MappingIterable<>(actual, mapper);
    }

    private static class MappingIterable<T, U> implements Iterable<U> {
        private final T[] input;
        private final Function<T, U> mapper;

        MappingIterable(T[] input, Function<T, U> mapper) {
            this.input = input;
            this.mapper = mapper;
        }

        @Override
        public Iterator<U> iterator() {
            return new MappingIterator<>(input, mapper);
        }
    }

    private static class MappingIterator<T, U> implements Iterator<U> {
        private final T[] input;
        private final Function<T, U> mapper;
        private int currentIndex = 0;

        MappingIterator(T[] input, Function<T, U> mapper) {
            this.input = input;
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < input.length;
        }

        @Override
        public U next() {
            return mapper.apply(input[currentIndex++]);
        }
    }

    @Deprecated
    public static <T, U> Matcher<T[]> arrayMappedWith(Function<T, U> mapper, Matcher<Iterable<? super U>> matcher) {
        return new ArrayMappingMatcher<>(mapper, matcher);
    }
}
