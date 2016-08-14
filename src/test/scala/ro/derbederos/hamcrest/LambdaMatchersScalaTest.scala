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

import java.util
import java.util.function.Function

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers._
import org.junit.Test
import ro.derbederos.hamcrest.LambdaMatchers.mappedBy
import ro.derbederos.hamcrest.LambdaMatchersScala.{asIterableMatcher, mapIterable, mappedBy => mappedByScala}
import ro.derbederos.hamcrest.LambdaMatchersTest.Person

import scala.collection.JavaConverters.asJavaIterable

class LambdaMatchersScalaTest {

  @Test def personTestMap(): Unit = {
    val person = new Person("Alice", 22)
    val getName: Function[Person, String] = p => p.getName
    assertThat(person, mappedBy[Person, String](getName, startsWith("A")))
    val getAge: Function[Person, Int] = p => p.getAge
    assertThat(person, mappedBy[Person, Int](getAge, equalTo(22)))
  }

  @Test def personTestMapScala(): Unit = {
    val person = new Person("Alice Bob", 21)
    assertThat(person, mappedByScala((p: Person) => p.getName, startsWith("A")))
    assertThat(person, mappedByScala((p: Person) => p.getAge, equalTo(21)))
  }

  @Test def personTestMapList(): Unit = {
    val p0 = new Person("Alice Bob", 21)
    val p1 = new Person("Ana Pop", 21)
    val p2 = new Person("Ariana G", 21)
    val list: util.List[LambdaMatchersTest.Person] = util.Arrays.asList(p0, p1, p2)

    assertThat(list, everyItem(mappedByScala((p: Person) => p.getAge, equalTo(21))))
    assertThat(list, everyItem(mappedByScala[Person, Int]((p: Person) => p.getAge, equalTo(21))))
    assertThat(list, hasItem(mappedByScala((p: Person) => p.getName, startsWith("Alice"))))
  }

  @Test def personTestMapScalaList(): Unit = {
    val p0 = new Person("Alice Bob", 21)
    val p1 = new Person("Ana Pop", 21)
    val p2 = new Person("Ariana G", 21)
    val list = List(p0, p1, p2)

    assertThat(asJavaIterable(list), hasItem(mappedByScala((p: Person) => p.getName, startsWith("Alice"))))
    // https://issues.scala-lang.org/browse/SI-5559
    //    assertThat(list, asIterableMatcher(hasItem(mappedByScala((p: Person) => p.getName, startsWith("Alice"))))) // compiler crashes

    //    assertThat(list, everyItem(mappedByScala[Person, Integer](p => p.getAge, equalTo(21))))
    //    assertThat(list, asIterableMatcher[Person](everyItem[Person](mappedBy[Person, Int]((p: Person) => p.getAge, equalTo(21)))))

    val getAge: Function[Person, Int] = p => p.getAge
    assertThat(list, asIterableMatcher[Person](hasItem[Person](mappedBy[Person, Int](getAge, equalTo(21)))))

    assertThat(list, mapIterable[Person, String]((p: Person) => p.getName, hasItem[String]("Ana Pop")))
    // https://issues.scala-lang.org/browse/SI-5559
    //    assertThat(list, mapIterable((p: Person) => p.getName, hasItem("Ana Pop"))) // compiler crashes
    //    assertThat(list, mapIterable[Person, _](p => p.getAge, hasItem[_](21))) // does not compile
    assertThat(list, mapIterable[Person, Int]((p: Person) => p.getAge, hasItem[Int](equalTo(21))))
    //    assertThat(list, mapIterable[Person, Integer](p => p.getAge, hasItem(equalTo(21)))) // does not compile
    //    assertThat(list, mapIterable[Person, Int](p => p.getAge, everyItem[Int](equalTo(21)))) // does not compile
  }

  @Test def stringTestMapScalaList(): Unit = {
    val listOfString = List("A", "B", "C")
    assertThat(listOfString, asIterableMatcher[String](hasItem[String](startsWith("A"))))
    //    assertThat(listOfString, asIterableMatcher[String](everyItem[String](startsWith("A"))))
  }
}