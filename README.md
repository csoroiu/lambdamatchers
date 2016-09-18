[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ro.derbederos.hamcrest/lambdamatchers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ro.derbederos.hamcrest/lambdamatchers)

[![Apache2 licensed](https://img.shields.io/badge/license-APACHE2-blue.svg)](https://raw.githubusercontent.com/csoroiu/lambdamatchers/master/LICENSE)

[![Build Status](https://travis-ci.org/csoroiu/lambdamatchers.svg?branch=master)](https://travis-ci.org/csoroiu/lambdamatchers)

# lambdamatchers
This library implements some hamcrest matchers usable with Java 8 and a set of utility functions built on top of them.

The usages of the matchers can be seen in:
* **[LambdaMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/LambdaMatchersTest.java)**. Some examples are:
```java
assertThat(list, everyItem(map(Person::getAge, greaterThanOrEqualTo(21))));

assertThat(list, hasItem(map(Person::getName, startsWith("Alice"))));

assertThat(list, mapIterable(Person::getName, hasItem("Ana")));

assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana"))));

assertThat(stream, mapStream(Person::getName, hasItem(startsWith("Ana"))));
```
* **[OptionalMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/OptionalMatchersTest.java)**
* **[RegexMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/RegexMatchersTest.java)**
