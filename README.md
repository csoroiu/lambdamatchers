# lambdamatchers
[![Maven Central][maven-tasks-svg]][maven-tasks-link]
[![Javadoc][javadoc-svg]][javadoc-link]
[![Build Status][build-status-svg]][build-status-link]
[![License][license-svg]][license-link]

This library implements some hamcrest matchers usable with **Java 8+ (8, 11, 17)** and a set of utility functions built on top of them.

**Les pièces de résistance** are the [LambdaMatchers](https://github.com/csoroiu/lambdamatchers/blob/master/src/main/java/ro/derbederos/hamcrest/LambdaMatchers.java)
**`hasFeature`** and **`assertFeature`** methods.

The library is featuring a [MatcherBuilder](https://github.com/csoroiu/lambdamatchers/blob/master/src/main/java/ro/derbederos/hamcrest/MatcherBuilder.java)
that enables one to easily create a new custom matcher.

## Features
* The matchers have meaningful descriptions. The library is intended to be used inside unit test and **help developers to get a better idea of what is wrong before looking at the source code**.
* The **`assertFeature`** method offers a way to maintain a simple test code while improving the error messages in case of failure.
* Lambda type detection, thanks to [Type Tools](http://github.com/jhalterman/typetools) library.
* Works with **Java 8 up to 17**.
* Compatible with [Hamcrest](https://github.com/hamcrest/JavaHamcrest) 2.2.

## Limitations
* Does **not** work on **Android** because of the dependencies.
* Matcher descriptions might not work fine for method references to *unboxing methods*,`Double::doubleValue`.
* When using agents that instrument the java byte code the type detection might malfunction. 
***In this unfortunate case I encourage you to fill in an issue about the problem you encountered.***

## Usage
#### Maven test dependency
```xml
<dependency>
    <groupId>ro.derbederos.hamcrest</groupId>
    <artifactId>lambdamatchers</artifactId>
    <version>0.19</version>
    <scope>test</scope>
</dependency>
```
#### Gradle test dependency
```groovy
testImplementation 'ro.derbederos.hamcrest:lambdamatchers:0.19'
```

#### Scala SBT test dependency
```sbt
libraryDependencies += "ro.derbederos.hamcrest" % "lambdamatchers" % "0.19" % "test"
```


## Examples
The usages of the matchers can be seen in:
* [RetryAtomic.java](https://gist.github.com/csoroiu/d982344e94b999d08f919737072fde5e)
* [OptionalMatchers.java](https://gist.github.com/csoroiu/098e51c01f57ecf7b599e7d1fd1b1d96)
* [LambdaMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/LambdaMatchersTest.java). Some examples are:
```java
assertFeature(person::getName, equalTo("Brutus"));

assertThat(list, everyItem(hasFeature(Person::getAge, greaterThanOrEqualTo(21))));

assertThat(list, hasItem(hasFeature(Person::getName, startsWith("Alice"))));

assertThat(list, featureIterable(Person::getName, hasItem("Ana")));

assertThat(array, featureArray(Person::getName, hasItem(startsWith("Ana"))));
```
* [StreamMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/StreamMatchersTest.java). Some examples are:
```java
assertThat(stream, featureStream(Person::getName, hasItem(startsWith("Ana"))));

assertThat(stream, toIterable(hasItem("Ana Pop"));

assertThat(Stream.empty(), emptyStream());
```
* [RegexMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/RegexMatchersTest.java)
* [RetryMatchersTest](https://github.com/csoroiu/lambdamatchers/blob/master/src/test/java/ro/derbederos/hamcrest/RetryMatchersTest.java). Some examples are:
```java
assertThat(mutableObject, retry(500, MutableObjectClass::getValue, equalTo(7)));

assertThat(bean, retry(300, hasProperty("value", equalTo(9))));

assertThat(atomicReferenceSpell, retry(500, AtomicReference::get, powerfulThan("Expecto Patronum")));

assertThat(atomicInteger, retry(300, AtomicInteger::intValue, equalTo(9)));

assertThat(atomicLong, retry(300, AtomicLong::longValue, greaterThan(10L)));
```

As already mentioned, this library is intended to be used in tests, and in case of failure, more meaningful descriptions are shown.

Such an error message for the code:
```java
assertFeature(person::getName, equalTo("Brutus"));
```
could be:
```java
java.lang.AssertionError: 
Expected: a `Person::getName` "Brutus"
     but: `Person::getName` was "Caesar"
```

And for the code:
```java
assertThat(list, everyItem(hasFeature(Person::getAge, greaterThanOrEqualTo(22))));
```
could be:
```java
java.lang.AssertionError: 
Expected: every item is a Person having `Person::getAge` a value equal to or greater than <22>
     but: an item `Person::getAge` <21> was less than <22>
```

An example a custom matcher creation:
```java
public Matcher<Person> myCustomMatcher(Person expected) {
    return MatcherBuilder
            .of(Person.class)
            .matches(expected, TestClass::myCustomEquals)
            .description(expected::getName)
            .describeMismatch(actual -> "was " + actual.getName())
            .build();
}
```

**Happy coding, and most important, happy testing!**

[build-status-svg]: https://app.travis-ci.com/csoroiu/lambdamatchers.svg?branch=master
[build-status-link]: https://app.travis-ci.com/csoroiu/lambdamatchers
[javadoc-svg]: http://javadoc.io/badge/ro.derbederos.hamcrest/lambdamatchers.svg?color=red
[javadoc-link]: http://javadoc.io/doc/ro.derbederos.hamcrest/lambdamatchers
[license-svg]: https://img.shields.io/badge/license-Apache2-blue.svg
[license-link]: https://raw.githubusercontent.com/csoroiu/lambdamatchers/master/LICENSE
[maven-tasks-svg]: https://img.shields.io/maven-central/v/ro.derbederos.hamcrest/lambdamatchers.svg
[maven-tasks-link]: https://search.maven.org/search?q=g:%22ro.derbederos.hamcrest%22%20AND%20a:%22lambdamatchers%22
