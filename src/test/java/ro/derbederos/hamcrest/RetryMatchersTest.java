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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static ro.derbederos.hamcrest.RetryMatchers.*;


public class RetryMatchersTest {
    @Rule
    public Timeout TIMEOUT = Timeout.millis(700);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(500, DelayedValueBean::getValue, equalTo(7)));
    }

    @Test
    public void testRetryFailsHasProperty() throws Exception {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: hasProperty(\"value\", <9>)");
        expectedException.expectMessage("     but: timed out after 300000000 property 'value' was <7>");

        DelayedValueBean bean = new DelayedValueBean(100, 2, 7);
        assertThat(bean, retry(300, hasProperty("value", equalTo(9))));
    }

    @Test
    public void testRetryFailsLambda() throws Exception {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a DelayedValueBean::Integer <9>");
        expectedException.expectMessage("     but: timed out after 300000000 Integer was <7>");

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
    public void testRetryAtomicIntegerFails() throws Exception {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: an AtomicInteger::Integer <9>");
        expectedException.expectMessage("     but: timed out after 300000000 Integer was <7>");

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

    private void executeDelayed(long delayMillis, Runnable runnable) {
        ForkJoinPool.commonPool().submit(() -> {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(delayMillis));
            runnable.run();
        });
    }

    public static class DelayedValueBean {
        private final long start = System.nanoTime();
        private final long delayNanos;
        private final int badValue;
        private final int goodValue;

        DelayedValueBean(long delayMillis, int badValue, int goodValue) {
            this.delayNanos = TimeUnit.MILLISECONDS.toNanos(delayMillis);
            this.badValue = badValue;
            this.goodValue = goodValue;
        }

        public int getValue() {
            if (System.nanoTime() - start < delayNanos) {
                return badValue;
            } else {
                return goodValue;
            }
        }
    }
}