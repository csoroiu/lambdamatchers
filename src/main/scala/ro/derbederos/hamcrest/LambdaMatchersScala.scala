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

import java.lang
import java.util.Objects

import _shaded.net.jodah.typetools.TypeResolver
import org.hamcrest.Matcher
import ro.derbederos.hamcrest.MappedValueMatcher.getArticle

import scala.collection.JavaConverters.asJavaIterable

object LambdaMatchersScala {

  def mappedBy[T, U](mapper: Function1[_ >: T, _ <: U], matcher: Matcher[_ >: U]): Matcher[T] = {
    Objects.requireNonNull(mapper)
    val featureType: Class[_] = TypeResolver.resolveRawArguments(classOf[Function1[_, _]], mapper.getClass)(1)
    var featureTypeName: String = featureType.getSimpleName
    if (classOf[TypeResolver.Unknown].isAssignableFrom(featureType)) {
      featureTypeName = "UnknownFieldType"
    } else {
      val methodRefString: String = MethodRefResolver.resolveMethodRefName(mapper.getClass)
      if (methodRefString != null) featureTypeName = methodRefString
    }
    mappedBy(mapper, featureTypeName, matcher)
  }

  private def mappedBy[T, U](mapper: Function1[_ >: T, _ <: U], featureTypeName: String, matcher: Matcher[_ >: U]): Matcher[T] = {
    Objects.requireNonNull(mapper)
    var inputType: Class[_] = TypeResolver.resolveRawArguments(classOf[Function1[_, _]], mapper.getClass)(0)
    var objectTypeName: String = inputType.getSimpleName
    if (classOf[TypeResolver.Unknown].isAssignableFrom(inputType)) {
      inputType = classOf[Any]
      objectTypeName = "UnknownObjectType"
    }
    val featureDescription = getArticle(objectTypeName) + " " + objectTypeName + " having " + featureTypeName
    new MappedValueMatcher[T, U](mapper(_), inputType, featureDescription, featureTypeName, matcher)
  }

  def mapIterable[T, U](mapper: Function1[_ >: T, _ <: U], matcher: Matcher[lang.Iterable[_ >: U]]): Matcher[Iterable[T]] = {
    val mapper1:(Iterable[_ <: T]) => lang.Iterable[_ >: U] = iterable => asJavaIterable(iterable.map(mapper))
    mappedBy(mapper1, matcher)
  }

  def asIterableMatcher[T](matcher: Matcher[lang.Iterable[_ >: T]]): Matcher[Iterable[_ <: T]] = {
    val mapper: (Iterable[_ <: T]) => lang.Iterable[_ >: T] = iterable => asJavaIterable(iterable)
    mappedBy(mapper, matcher)
  }
}
