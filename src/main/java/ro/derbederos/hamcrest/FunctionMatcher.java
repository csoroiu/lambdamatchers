package ro.derbederos.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Objects;
import java.util.function.Function;

class FunctionMatcher<T, U> extends FeatureMatcher<T, U> {

    private final Function<T, U> mapper;
    private T lastChecked;
    private U lastResult;

    private FunctionMatcher(Function<T, U> mapper, Matcher<? super U> subMatcher) {
        super(subMatcher, "Feature " + extractFeatureName(mapper), extractFeatureName(mapper));
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(subMatcher);
        this.mapper = mapper;
    }

    private static <T, U> String extractFeatureName(Function<T, U> mapper) {
        return mapper.getClass().getSimpleName();
    }

    @Override
    protected U featureValueOf(T actual) {
        if (lastChecked == null || lastChecked != actual) {
            lastResult = mapper.apply(actual);
            lastChecked = actual;
        }
        return lastResult;
    }

    @Override
    protected boolean matchesSafely(T actual, Description mismatch) {
        boolean result = super.matchesSafely(actual, mismatch);
        // clear the state
        // first call on matchesSafely is done with a Description.NullDescription
        if (result || !(mismatch instanceof Description.NullDescription)) {
            lastChecked = null;
            lastResult = null;
        }
        return result;
    }

    public static <T, U> Matcher<T> map(Function<T, U> mapper, Matcher<? super U> matcher) {
        return new FunctionMatcher<>(mapper, matcher);
    }
}
