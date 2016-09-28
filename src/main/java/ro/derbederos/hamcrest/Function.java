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

/**
 * Represents a function that accepts one argument and produces a result.
 * <p>
 * This is a functional interface whose functional method is {@link #apply(Object)}.
 * <br>
 * Signature of {@link #apply(Object)} method is similar to {@link java.util.function.Function#apply(Object)}
 * <br>
 * This class is needed only to be compatible with Java 6 and 7.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @since 0.8
 */
public interface Function<T, R> {
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);
}
