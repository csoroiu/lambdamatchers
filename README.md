# lambdamatchers
[![Build Status][build-status-svg]][build-status-link]
[![Maven Central][maven-tasks-svg]][maven-tasks-link]
[![License][license-svg]][license-link]

This library implements some hamcrest matchers usable with Java 8 and a set of utility functions built on top of them.
Considering the fact that this library is intended to be used in tests, in case of failure, meaningful messages are shown in order to help figuring out what is wrong even before looking at the source code.

Such an error message for the code:
```java
assertThat(list, everyItem(map(Person::getAge, greaterThanOrEqualTo(22))));
```
could be:
```
java.lang.AssertionError: 
Expected: every item is a Person having `int Person.getAge()` a value equal to or greater than <22>
     but: an item `int Person.getAge()` <21> was less than <22>
```

The usages of the matchers can be seen in:
* **[LambdaMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/LambdaMatchersTest.java)**. Some examples are:
```java
assertThat(list, everyItem(map(Person::getAge, greaterThanOrEqualTo(21))));

assertThat(list, hasItem(map(Person::getName, startsWith("Alice"))));

assertThat(list, mapIterable(Person::getName, hasItem("Ana")));

assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana"))));
```
* **[StreamMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/StreamMatchersTest.java)**. Some examples are:
```java
assertThat(stream, mapStream(Person::getName, hasItem(startsWith("Ana"))));

assertThat(stream, toIterable(hasItem("Ana Pop"));

assertThat(Stream.empty(), emptyStream());
```
* **[OptionalMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/OptionalMatchersTest.java)**
* **[RegexMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/RegexMatchersTest.java)**
* **[RetryMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/RetryMatchersTest.java)**. Some examples are:
```java
assertThat(mutableObject, retry(500, a -> a.getValue(), equalTo(7)));

assertThat(bean, retry(300, hasProperty("value", equalTo(9))));

assertThat(atomicReferenceSpell, retryAtomicReference(500, powerfulThan("Expecto Patronum")));

assertThat(atomicInteger, retryAtomicInteger(300, 9));

assertThat(atomicLong, retryAtomicLong(300, greaterThan(10L)));
```

[build-status-svg]: https://travis-ci.org/csoroiu/lambdamatchers.svg?branch=master
[build-status-link]: https://travis-ci.org/csoroiu/lambdamatchers
[license-svg]: https://img.shields.io/badge/license-Apache2-blue.svg
[license-link]: https://raw.githubusercontent.com/csoroiu/lambdamatchers/master/LICENSE
[maven-tasks-svg]: https://img.shields.io/maven-central/v/ro.derbederos.hamcrest/lambdamatchers.svg
[maven-tasks-link]: https://maven-badges.herokuapp.com/maven-central/ro.derbederos.hamcrest/lambdamatchers
