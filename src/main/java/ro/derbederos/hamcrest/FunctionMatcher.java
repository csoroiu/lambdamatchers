package ro.derbederos.hamcrest;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.function.Function;

class FunctionMatcher<T, U> extends FeatureMatcher<T, U> {

    private final Function<T, U> mapper;

    private FunctionMatcher(Function<T, U> mapper, Matcher<? super U> subMatcher) {
        super(subMatcher, "Feature", "");
        this.mapper = mapper;
    }

    @Override
    protected U featureValueOf(T actual) {
        return mapper.apply(actual);
    }

    public static <T, U> Matcher<T> mappedWith(Function<T, U> mapper, Matcher<? super U> matcher) {
        return new FunctionMatcher<>(mapper, matcher);
    }
}
