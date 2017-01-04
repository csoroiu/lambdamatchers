/*
 * Copyright (c) 2016-2017 Claudiu Soroiu
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertDescription;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertMismatchDescription;
import static ro.derbederos.hamcrest.RetryMatchers.*;

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

    private static void executeDelayed(long delayMillis, Runnable runnable) {
        executorService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testRetryHasProperty() throws Exception {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(500, 25, hasProperty("value", equalTo(7))));
    }

    @Test
    public void testRetryTimeUnitHasProperty() throws Exception {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(500, TimeUnit.MILLISECONDS, hasProperty("value", equalTo(7))));
    }

    @Test
    public void testRetryLambda() throws Exception {
        DelayedValueBean bean = new DelayedValueBean(144, 2, 7);
        assertThat(bean, retry(500, DelayedValueBean::getValue, equalTo(7)));
        assertThat(bean.getValueCallCount.intValue(), greaterThan(2));
    }

    @Test
    public void testNoRetryLambda() throws Exception {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(500, DelayedValueBean::getValue, equalTo(2)));
        assertThat(bean.getValueCallCount.intValue(), equalTo(1));
    }

    @Test
    public void testRetryHasPropertyDescription() throws Exception {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        Matcher<Object> retryMatcher = retry(300, hasProperty("value", equalTo(9)));
        assertDescription(equalTo("hasProperty(\"value\", <9>)"), retryMatcher);
        assertMismatchDescription(equalTo("after 300 millisecond(s) property 'value' was <7>"),
                bean, retryMatcher);
    }

    @Test
    public void testRetryLambdaDescription() throws Exception {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        Matcher<DelayedValueBean> retryMatcher = retry(300, DelayedValueBean::getValue, equalTo(9));
        assertDescription(equalTo("a DelayedValueBean having `int DelayedValueBean.getValue()` <9>"), retryMatcher);
        assertMismatchDescription(equalTo("after 300 millisecond(s) `int DelayedValueBean.getValue()` was <7>"),
                bean, retryMatcher);
    }

    @Test
    public void testRetryAtomicInteger() throws Exception {
        AtomicInteger atomicInteger = new AtomicInteger(2);
        executeDelayed(100, () -> atomicInteger.set(7));
        assertThat(atomicInteger, retryAtomicInteger(500, 7));
    }

    @Test
    public void testRetryAtomicIntegerDescription() throws Exception {
        AtomicInteger atomicInteger = new AtomicInteger(7);
        Matcher<AtomicInteger> retryMatcher = retryAtomicInteger(300, 9);
        assertDescription(equalTo("an AtomicInteger having `int AtomicInteger.intValue()` <9>"), retryMatcher);
        assertMismatchDescription(equalTo("after 300 millisecond(s) `int AtomicInteger.intValue()` was <7>"),
                atomicInteger, retryMatcher);
    }

    @Test
    public void testRetryAtomicLong() throws Exception {
        AtomicLong atomicLong = new AtomicLong(2);
        executeDelayed(100, () -> atomicLong.set(7));
        assertThat(atomicLong, retryAtomicLong(500, 7L));
    }

    @Test
    public void testRetryAtomicDouble() throws Exception {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        executeDelayed(100, () -> atomicBoolean.set(true));
        assertThat(atomicBoolean, retryAtomicBoolean(500, true));
    }

    @Test
    public void testRetryAtomicReference() throws Exception {
        AtomicReference<String> atomicString = new AtomicReference<>("Expelliarmus");
        executeDelayed(100, () -> atomicString.set("Expecto Patronum"));
        assertThat(atomicString, retryAtomicReference(500, "Expecto Patronum"));
    }

    @Test
    public void lambdaAssertSimpleTestObjectMethodReference() {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        lambdaAssert(bean::getValue, 500, equalTo(7));
    }

    @Test
    public void lambdaAssertSimpleTestObjectMethodReferenceDescription() {
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        Matcher<Supplier<Integer>> retryMatcher = retrySupplier(300, bean::getValue, equalTo(9));

        assertDescription(equalTo("a `int DelayedValueBean.getValue()` <9>"), retryMatcher);
        assertMismatchDescription(equalTo("after 300 millisecond(s) `int DelayedValueBean.getValue()` was <7>"),
                bean::getValue, retryMatcher);
    }

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