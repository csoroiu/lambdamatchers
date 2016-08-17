[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/csoroiu/lambdamatchers/master/LICENSE)
[![Build Status](https://travis-ci.org/csoroiu/lambdamatchers.svg?branch=master)](https://travis-ci.org/csoroiu/lambdamatchers)

# lambdamatchers
This library implements some hamcrest matchers usable with Java 8 and a set of utility functions built on top of them.

The matchers implemented are:
* **[FunctionMatcher](https://github.com/csoroiu/lambdamatchers/blob/master/src/main/java/ro/derbederos/hamcrest/FunctionMatcher.java)** - a mapping matcher, accessible via various **[LambdaMatchers](https://github.com/csoroiu/lambdamatchers/blob/master/src/main/java/ro/derbederos/hamcrest/LambdaMatchers.java)** methods
* **[RegexMatcher](https://github.com/csoroiu/lambdamatchers/blob/master/src/main/java/ro/derbederos/hamcrest/RegexMatcher.java)** - a regex pattern matcher, accessible via overloaded **[RegexMatcher.matchesPattern](https://github.com/csoroiu/lambdamatchers/blob/master/src/main/java/ro/derbederos/hamcrest/RegexMatcher.java#L36)** and **[RegexMatcher.containsPattern](https://github.com/csoroiu/lambdamatchers/blob/master/src/main/java/ro/derbederos/hamcrest/RegexMatcher.java#L56)** methods

The usages van be seen in:
* **[LambdaMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/LambdaMatchersTest.java)**. Some examples are:
```java
assertThat(list, everyItem(map(Person::getAge, greaterThanOrEqualTo(21))));

assertThat(list, hasItem(map(Person::getName, startsWith("Alice"))));

assertThat(list, mapIterable(Person::getName, hasItem("Ana")));

assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana"))));

assertThat(stream, mapStream(Person::getName, hasItem(startsWith("Ana"))));
```
* **[RegexMatcherTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/RegexMatcherTest.java)**
