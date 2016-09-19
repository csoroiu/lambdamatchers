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

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static ro.derbederos.hamcrest.LambdaMatchers.map;
import static ro.derbederos.hamcrest.RetryMatcher.retry;


public class RetryMatcherTest {
    @Rule
    public Timeout TIMEOUT = Timeout.millis(1000);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRetryHasProperty() throws Exception {
        DelayedValueBean bean = new DelayedValueBean(500, 2, 7);
        assertThat(bean, retry(750, hasProperty("value", equalTo(7))));
    }

    @Test
    public void testRetryTimeUnitHasProperty() throws Exception {
        DelayedValueBean bean = new DelayedValueBean(500, 2, 7);
        assertThat(bean, retry(750, TimeUnit.MILLISECONDS, hasProperty("value", equalTo(7))));
    }


    @Test
    public void testRetryLambda() throws Exception {
        DelayedValueBean bean = new DelayedValueBean(500, 2, 7);
        assertThat(bean, retry(750, map(DelayedValueBean::getValue, equalTo(7))));
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
        assertThat(bean, retry(300, map(DelayedValueBean::getValue, equalTo(9))));

    }


    public static class DelayedValueBean {
        private final long start = System.nanoTime();
        private final long delayNanos;
        private final int badValue;
        private final int goodValue;

        DelayedValueBean(long delayMillis, int badValue, int goodValue) {
            this(delayMillis, TimeUnit.MILLISECONDS, badValue, goodValue);
        }

        DelayedValueBean(long delay, TimeUnit timeUnit, int badValue, int goodValue) {
            this.delayNanos = timeUnit.toNanos(delay);
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