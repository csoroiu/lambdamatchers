# lambdamatchers
[![Build Status][build-status-svg]][build-status-link]
[![Maven Central][maven-tasks-svg]][maven-tasks-link]
[![License][license-svg]][license-link]

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

[build-status-svg]: https://travis-ci.org/csoroiu/lambdamatchers.svg?branch=master
[build-status-link]: https://travis-ci.org/csoroiu/lambdamatchers
[license-svg]: https://img.shields.io/badge/license-APACHE2-blue.svg
[license-link]: https://raw.githubusercontent.com/csoroiu/lambdamatchers/master/LICENSE
[maven-tasks-svg]: https://img.shields.io/maven-central/v/ro.derbederos.hamcrest/lambdamatchers.svg
[maven-tasks-link]: https://maven-badges.herokuapp.com/maven-central/ro.derbederos.hamcrest/lambdamatchers
