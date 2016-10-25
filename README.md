# lambdamatchers
[![Build Status][build-status-svg]][build-status-link]
[![Maven Central][maven-tasks-svg]][maven-tasks-link]
[![License][license-svg]][license-link]

This library implements some hamcrest matchers usable with Java 8 and a set of utility functions built on top of them.

## Usage
#### Maven dependency
```
<dependency>
    <groupId>ro.derbederos.hamcrest</groupId>
    <artifactId>lambdamatchers</artifactId>
    <version>0.7</version>
    <scope>test</scope>
</dependency>
```
#### Gradle dependency
```
testCompile 'ro.derbederos.hamcrest:lambdamatchers:0.7'
```

## Examples
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

This library is intended to be used in tests, and in case of failure, **meaningful descriptions** are shown in order to ***help the developers to get an idea of what is wrong before looking at the source code***.

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

## Features
* The matchers have **meaningful descriptions**. The library is intended to be used inside unit test and ***help the developers to get an idea of what is wrong before looking at the source code***.
* Lambda type detection, thanks to **[Type Tools](http://github.com/jhalterman/typetools)** library.
* Built for Java 8. There are plans to support Java 5, 6, 7 via **[Retrolambda](https://github.com/orfjackal/retrolambda)**.
* Compatible with **[Hamcrest](https://github.com/hamcrest/JavaHamcrest)** versions **1.3** and **2.0**, even if it is compiled against version **1.3**.

**Happy coding!**

## Limitations
* Does **not** work on **Android** and **Java 9** because of the dependencies. ***This might be improved if required.***
* Matcher descriptions might not work ok for `Object` class method references such as `Object::toString`.
* When using agents that instrument the java byte code the type detection might malfunction. 
***In this unfortunate case I encourage you to fill in an issue about the problem you encountered.***

[build-status-svg]: https://travis-ci.org/csoroiu/lambdamatchers.svg?branch=master
[build-status-link]: https://travis-ci.org/csoroiu/lambdamatchers
[license-svg]: https://img.shields.io/badge/license-Apache2-blue.svg
[license-link]: https://raw.githubusercontent.com/csoroiu/lambdamatchers/master/LICENSE
[maven-tasks-svg]: https://img.shields.io/maven-central/v/ro.derbederos.hamcrest/lambdamatchers.svg
[maven-tasks-link]: https://maven-badges.herokuapp.com/maven-central/ro.derbederos.hamcrest/lambdamatchers
