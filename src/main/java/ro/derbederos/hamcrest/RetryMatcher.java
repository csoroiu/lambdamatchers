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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

final class RetryMatcher<T> extends BaseMatcher<T> {

    private final long durationNanos;
    private final long intervalNanos;
    private final Matcher<? super T> subMatcher;

    private RetryMatcher(long durationNanos, long intervalNanos, Matcher<? super T> subMatcher) {
        this.durationNanos = durationNanos;
        this.intervalNanos = intervalNanos;
        this.subMatcher = subMatcher;
    }

    @Override
    public boolean matches(Object item) {
        final long start = System.nanoTime();
        while (!subMatcher.matches(item)) {
            final long elapsed = System.nanoTime() - start;
            if (elapsed >= durationNanos) {
                return false;
            }
            LockSupport.parkNanos(intervalNanos);
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        subMatcher.describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText("timed out after " + durationNanos + " ");
        subMatcher.describeMismatch(item, description);
    }

    static <T> Matcher<T> retry(long duration, long interval, TimeUnit timeUnit, Matcher<? super T> subMatcher) {
        return new RetryMatcher<T>(timeUnit.toNanos(duration), timeUnit.toNanos(interval), subMatcher);
    }

    static <T> Matcher<T> retry(long duration, TimeUnit timeUnit, Matcher<? super T> subMatcher) {
        return new RetryMatcher<T>(timeUnit.toNanos(duration), TimeUnit.MILLISECONDS.toNanos(50), subMatcher);
    }

    static <T> Matcher<T> retry(long durationMillis, Matcher<? super T> subMatcher) {
        return new RetryMatcher<T>(TimeUnit.MILLISECONDS.toNanos(durationMillis), TimeUnit.MILLISECONDS.toNanos(50), subMatcher);
    }
}
