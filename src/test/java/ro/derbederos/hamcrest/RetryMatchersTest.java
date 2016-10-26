/*
 * Copyright (c) 2016 Claudiu Soroiu
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import static ro.derbederos.hamcrest.RetryMatchers.*;

public class RetryMatchersTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

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
    public void testRetryHasPropertyAssertionError() throws Exception {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: hasProperty(\"value\", <9>)");
        expectedException.expectMessage("     but: after 300 millisecond(s) property 'value' was <7>");

        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(300, hasProperty("value", equalTo(9))));
    }

    @Test
    public void testRetryLambdaAssertionError() throws Exception {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a DelayedValueBean having `int DelayedValueBean.getValue()` <9>");
        expectedException.expectMessage("     but: after 300 millisecond(s) `int DelayedValueBean.getValue()` was <7>");

        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(300, DelayedValueBean::getValue, equalTo(9)));
    }

    @Test
    public void testRetryAtomicInteger() throws Exception {
        AtomicInteger atomicInteger = new AtomicInteger(2);
        executeDelayed(100, () -> atomicInteger.set(7));
        assertThat(atomicInteger, retryAtomicInteger(500, 7));
    }

    @Test
    public void testRetryAtomicIntegerAssertionError() throws Exception {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: an AtomicInteger having `int AtomicInteger.intValue()` <9>");
        expectedException.expectMessage("     but: after 300 millisecond(s) `int AtomicInteger.intValue()` was <7>");

        AtomicInteger atomicInteger = new AtomicInteger(2);
        executeDelayed(100, () -> atomicInteger.set(7));
        assertThat(atomicInteger, retryAtomicInteger(300, 9));
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