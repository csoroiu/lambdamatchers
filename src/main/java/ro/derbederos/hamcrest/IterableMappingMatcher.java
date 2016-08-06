package ro.derbederos.hamcrest;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Iterator;
import java.util.function.Function;

@Deprecated
class IterableMappingMatcher<T, U> extends FeatureMatcher<Iterable<? extends T>, Iterable<? super U>> {

    private final Function<T, U> mapper;

    private IterableMappingMatcher(Function<T, U> mapper, Matcher<Iterable<? super U>> subMatcher) {
        super(subMatcher, "Feature", "");
        this.mapper = mapper;
    }

    @Override
    protected Iterable<U> featureValueOf(Iterable<? extends T> actual) {
        return new MappingIterable<>(actual, mapper);
    }

    private static class MappingIterable<T, U> implements Iterable<U> {
        private final Iterable<? extends T> input;
        private final Function<T, U> mapper;

        MappingIterable(Iterable<? extends T> input, Function<T, U> mapper) {
            this.input = input;
            this.mapper = mapper;
        }

        @Override
        public Iterator<U> iterator() {
            return new MappingIterator<>(input.iterator(), mapper);
        }
    }

    private static class MappingIterator<T, U> implements Iterator<U> {
        private final Iterator<? extends T> iterator;
        private final Function<T, U> mapper;

        MappingIterator(Iterator<? extends T> iterator, Function<T, U> mapper) {
            this.iterator = iterator;
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public U next() {
            return mapper.apply(iterator.next());
        }
    }

    public static <T, U> Matcher<Iterable<? extends T>> mappedWith(Function<T, U> mapper, Matcher<Iterable<? super U>> matcher) {
        return new IterableMappingMatcher<>(mapper, matcher);
    }
}
