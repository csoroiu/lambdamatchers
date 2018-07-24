/*
 * Copyright (c) 2016-2018 Claudiu Soroiu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.derbederos.hamcrest;

import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasProperty;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertDescription;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertMismatchDescription;
import static ro.derbederos.hamcrest.RetryMatchers.assertFeature;
import static ro.derbederos.hamcrest.RetryMatchers.retry;
import static ro.derbederos.hamcrest.RetryMatchers.retrySupplier;
import static ro.derbederos.hamcrest.TestUtil.assumeJavaVersion;

public class RetryMatchersTest {
    @Rule
    public Timeout TIMEOUT = Timeout.millis(700);

    private static ScheduledExecutorService executorService;

    @BeforeClass
    public static void beforeRetryMatchersTest() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @AfterClass
    public static void afterRetryMatchersTest() throws Exception {
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("SameParameterValue")
    private static void executeDelayed(long delayMillis, Runnable runnable) {
        executorService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testRetryHasProperty() {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(500, 25, hasProperty("value", equalTo(7))));
    }

    @Test
    public void testRetryTimeUnitHasProperty() {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(500, TimeUnit.MILLISECONDS, hasProperty("value", equalTo(7))));
    }

    @Test
    public void testRetryLambda() {
        DelayedValueBean bean = new DelayedValueBean(144, 2, 7);
        assertThat(bean, retry(500, DelayedValueBean::getValue, equalTo(7)));
        assertThat(bean.getValueCallCount.intValue(), greaterThanOrEqualTo(2));
    }

    @Test
    public void testNoRetryLambda() {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(500, DelayedValueBean::getValue, equalTo(2)));
        assertThat(bean.getValueCallCount.intValue(), equalTo(1));
    }

    @Test
    public void testRetryHasPropertyDescription() {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        Matcher<Object> retryMatcher = retry(300, hasProperty("value", equalTo(9)));
        assertDescription(equalTo("hasProperty(\"value\", <9>)"), retryMatcher);
        assertMismatchDescription(equalTo("after 300 millisecond(s) property 'value' was <7>"),
                bean, retryMatcher);
    }

    @Test
    public void testRetryLambdaDescription() {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        Matcher<DelayedValueBean> retryMatcher = retry(300, DelayedValueBean::getValue, equalTo(9));
        assertDescription(equalTo("a DelayedValueBean having `DelayedValueBean::getValue` <9>"), retryMatcher);
        assertMismatchDescription(equalTo("after 300 millisecond(s) `DelayedValueBean::getValue` was <7>"),
                bean, retryMatcher);
    }

    @Test
    public void testRetryAtomicLong() {
        AtomicLong atomicLong = new AtomicLong(2);
        executeDelayed(100, () -> atomicLong.set(7));
        assertThat(atomicLong, retry(500, AtomicLong::longValue, equalTo(7L)));
    }

    @Test
    public void testRetryAtomicLongDescription() {
        AtomicLong atomicInteger = new AtomicLong(7);
        Matcher<AtomicLong> retryMatcher = retry(300, AtomicLong::longValue, equalTo(9L));
        assertDescription(equalTo("an AtomicLong having `AtomicLong::longValue` <9L>"), retryMatcher);
        assertMismatchDescription(equalTo("after 300 millisecond(s) `AtomicLong::longValue` was <7L>"),
                atomicInteger, retryMatcher);
    }

    @Test
    public void assertFeatureSimpleTestObjectMethodReference() {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertFeature(bean::getValue, 500, equalTo(7));
    }

    @Test
    public void assertFeatureSimpleTestObjectMethodReferenceDescription() {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        Matcher<Supplier<Integer>> retryMatcher = retrySupplier(300, bean::getValue, equalTo(9));

        assertDescription(equalTo("a `DelayedValueBean::getValue` <9>"), retryMatcher);
        assertMismatchDescription(equalTo("after 300 millisecond(s) `DelayedValueBean::getValue` was <7>"),
                bean::getValue, retryMatcher);
    }

    @Test
    public void testRetryLongAccumulator() {
        assumeJavaVersion(1.8);
        LongAccumulator accumulator = new LongAccumulator((a, b) -> a * b, 3);
        @SuppressWarnings("Convert2Lambda")
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                accumulator.accumulate(7);
            }
        };
        executeDelayed(100, runnable);
        assertThat(accumulator, retry(500, LongAccumulator::longValue, equalTo(21L)));
    }

    @Test
    public void testRetryLongAccumulatorDescription() {
        assumeJavaVersion(1.8);
        LongAccumulator accumulator = new LongAccumulator((a, b) -> a * b, 3);
        @SuppressWarnings("Convert2Lambda")
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                accumulator.accumulate(7);
            }
        };
        executeDelayed(100, runnable);
        Matcher<LongAccumulator> retryMatcher = retry(300, LongAccumulator::longValue, equalTo(30L));
        assertDescription(equalTo("a LongAccumulator having `LongAccumulator::longValue` <30L>"),
                retryMatcher);
        assertMismatchDescription(equalTo("after 300 millisecond(s) `LongAccumulator::longValue` was <21L>"),
                accumulator, retryMatcher);
    }

    @SuppressWarnings("WeakerAccess")
    public static class DelayedValueBean {
        private final long start = System.nanoTime();
        private final long delayNanos;
        private final int badValue;
        private final int goodValue;
        private final AtomicInteger getValueCallCount = new AtomicInteger();

        DelayedValueBean(long delayMillis, int badValue, int goodValue) {
            this.delayNanos = TimeUnit.MILLISECONDS.toNanos(delayMillis);
            this.badValue = badValue;
            this.goodValue = goodValue;
        }

        public int getValue() {
            getValueCallCount.incrementAndGet();
            if (System.nanoTime() - start < delayNanos) {
                return badValue;
            } else {
                return goodValue;
            }
        }
    }
}