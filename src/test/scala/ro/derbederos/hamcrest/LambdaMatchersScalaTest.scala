/*
 * Copyright (c) 2016-2017 Claudiu Soroiu
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

package ro.derbederos.hamcrest

import org.hamcrest.CoreMatchers.{equalTo, startsWith}
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers._
import org.junit.jupiter.api.{Disabled, Test}
import ro.derbederos.hamcrest.LambdaMatchers.hasFeature
import ro.derbederos.hamcrest.LambdaMatchersScala.{asJavaIterableMatcher, featureIterable, hasFeature => hasFeatureScala}
import ro.derbederos.hamcrest.LambdaMatchersTest.Person
import ro.derbederos.hamcrest.MatcherDescriptionAssert.{assertDescription, assertMismatchDescription}

import java.util
import java.util.function.Function
import scala.jdk.javaapi.CollectionConverters.asJava

class LambdaMatchersScalaTest {

  @Test def simpleTestObjectMethodReference(): Unit = {
    val p = new Person("Alice", 21)
    assertThat(p, hasFeatureScala((p: Person) => p.getName, startsWith("A")))
  }

  @Test @Disabled def simpleTestObjectMethodReferenceDescription(): Unit = {
    val featureMatcher = hasFeatureScala((_: Person).getName, startsWith("B"))
    assertDescription(equalTo("a Person having `Person::getName` a string starting with \"B\""), featureMatcher)
    assertMismatchDescription(equalTo("`Person::getName` was \"Alice\""), new Person("Alice", 21), featureMatcher)
  }

  @Test def personTestMap(): Unit = {
    val person = new Person("Alice", 22)
    val getName: Function[Person, String] = p => p.getName
    assertThat(person, hasFeature[Person, String](getName, startsWith("A")))
    val getAge: Function[Person, Int] = p => p.getAge
    assertThat(person, hasFeature[Person, Int](getAge, equalTo(22)))
  }

  @Test def personTestMapScala(): Unit = {
    val person = new Person("Alice Bob", 21)
    assertThat(person, hasFeatureScala((_: Person).getName, startsWith("A")))
    assertThat(person, hasFeatureScala((_: Person).getAge, equalTo(21)))
  }

  @Test def personTestMapList(): Unit = {
    val p0 = new Person("Alice Bob", 21)
    val p1 = new Person("Ana Pop", 21)
    val p2 = new Person("Ariana G", 21)
    val list: util.List[LambdaMatchersTest.Person] = util.Arrays.asList(p0, p1, p2)

    assertThat(list, everyItem(hasFeatureScala((p: Person) => p.getAge, equalTo(21))))
    assertThat(list, everyItem(hasFeatureScala[Person, Int]((p: Person) => p.getAge, equalTo(21))))
    assertThat(list, hasItem(hasFeatureScala((p: Person) => p.getName, startsWith("Alice"))))
  }

  @Test def personTestMapScalaList(): Unit = {
    val p0 = new Person("Alice Bob", 21)
    val p1 = new Person("Ana Pop", 21)
    val p2 = new Person("Ariana G", 21)
    val list = List(p0, p1, p2)

    assertThat(asJava(list), hasItem(hasFeatureScala((p: Person) => p.getName, startsWith("Alice"))))
    // https://issues.scala-lang.org/browse/SI-5559
    assertThat(list, asJavaIterableMatcher(hasItem(hasFeatureScala((_: Person).getName, startsWith("Alice"))))) // compiler crashes

    //    assertThat(list, everyItem(hasFeatureScala[Person, Integer](p => p.getAge, equalTo(21))))
    //    assertThat(list, asJavaIterableMatcher[Person](everyItem[Person](hasFeature[Person, Int]((p: Person) => p.getAge, equalTo(21)))))

    val getAge: Function[Person, Int] = p => p.getAge
    assertThat(list, asJavaIterableMatcher[Person](hasItem[Person](hasFeature[Person, Int](getAge, equalTo(21)))))

    assertThat(list, featureIterable[Person, String]((p: Person) => p.getName, hasItem[String]("Ana Pop")))
    // https://issues.scala-lang.org/browse/SI-5559
    //    assertThat(list, featureIterable((p: Person) => p.getName, hasItem("Ana Pop"))) // compiler crashes
    //    assertThat(list, featureIterable[Person, _]((p: Person) => p.getAge, hasItem[_](21))) // does not compile
    assertThat(list, featureIterable[Person, Int]((p: Person) => p.getAge, hasItem[Int](equalTo(21))))
    //    assertThat(list, featureIterable[Person, Integer](p => p.getAge, hasItem(equalTo(21)))) // does not compile
    //    assertThat(list, featureIterable[Person, Int](p => p.getAge, everyItem[Int](equalTo(21)))) // does not compile
  }

  @Test def stringTestMapScalaList(): Unit = {
    val listOfString = List("A", "B", "C")
    assertThat(listOfString, asJavaIterableMatcher[String](hasItem[String](startsWith("A"))))
    //    assertThat(listOfString, asIterableMatcher[String](everyItem[String](startsWith("A"))))
  }
}