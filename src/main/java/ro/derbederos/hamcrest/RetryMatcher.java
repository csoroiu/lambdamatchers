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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

final class RetryMatcher<T> extends BaseMatcher<T> {

    private static final int DEFAULT_INTERVAL_MILLIS = 50;

    private final TimeUnit timeUnit;
    private final long durationNanos;
    private final long intervalNanos;
    private final Matcher<? super T> subMatcher;

    private RetryMatcher(long durationNanos, long intervalNanos, TimeUnit timeUnit, Matcher<? super T> subMatcher) {
        this.timeUnit = timeUnit;
        this.durationNanos = timeUnit.toNanos(durationNanos);
        this.intervalNanos = timeUnit.toNanos(intervalNanos);
        this.subMatcher = subMatcher;
    }

    @Override
    public boolean matches(Object item) {
        final long start = System.nanoTime();
        final long parkTime = Math.min(this.intervalNanos, durationNanos);

        while (!threadSafeMatches(item)) {
            final long elapsed = System.nanoTime() - start;
            if (elapsed >= durationNanos) {
                return false;
            }
            LockSupport.parkNanos(parkTime);
        }
        return true;
    }

    private final Object LOCK = new Object();

    private boolean threadSafeMatches(Object item) {
        synchronized (LOCK) {
            return subMatcher.matches(item);
        }
    }

    @Override
    public void describeTo(Description description) {
        subMatcher.describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        final long duration = timeUnit.convert(durationNanos, TimeUnit.NANOSECONDS);
        final String timeUnitStr = timeUnit.toString().toLowerCase().replaceAll("s$", "(s)");
        description.appendText("after " + duration + " " + timeUnitStr);
        subMatcher.describeMismatch(item, description);
    }

    static <T> Matcher<T> retry(long duration, long interval, TimeUnit timeUnit, Matcher<? super T> subMatcher) {
        return new RetryMatcher<>(duration, interval, timeUnit, subMatcher);
    }

    static <T> Matcher<T> retry(long duration, TimeUnit timeUnit, Matcher<? super T> subMatcher) {
        return retry(duration, TimeUnit.MILLISECONDS.convert(DEFAULT_INTERVAL_MILLIS, timeUnit), timeUnit, subMatcher);
    }
}
