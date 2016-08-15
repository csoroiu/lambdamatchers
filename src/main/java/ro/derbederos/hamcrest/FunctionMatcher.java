package ro.derbederos.hamcrest;

import net.jodah.typetools.TypeResolver;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Objects;
import java.util.function.Function;

final class FunctionMatcher<T, U> extends FeatureMatcher<T, U> {

    private final Function<T, U> mapper;
    private T lastChecked;
    private U lastResult;

    FunctionMatcher(Function<T, U> mapper, String typeName, String feature, Matcher<? super U> subMatcher) {
        super(subMatcher, "a " + typeName + " with feature of type " + feature, feature);
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(subMatcher);
        this.mapper = mapper;
    }

    @Override
    protected final U featureValueOf(T actual) {
        if (lastChecked == null || lastChecked != actual) {
            lastResult = mapper.apply(actual);
            lastChecked = actual;
        }
        return lastResult;
    }

    @Override
    protected final boolean matchesSafely(T actual, Description mismatch) {
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
        Class<?>[] arguments = TypeResolver.resolveRawArguments(Function.class, mapper.getClass());
        String objectTypeName = arguments[0].getSimpleName().toLowerCase();
        String featureTypeName = arguments[1].getSimpleName().toLowerCase();
        return new FunctionMatcher<>(mapper, objectTypeName, featureTypeName, matcher);
    }
}