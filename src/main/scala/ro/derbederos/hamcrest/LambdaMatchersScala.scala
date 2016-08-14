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

import org.hamcrest.Matcher

import java.lang
import scala.jdk.javaapi.CollectionConverters.asJava

object LambdaMatchersScala {

  def hasFeature[T, U](featureName: String, featureExtractor: (_ >: T) => _ <: U, featureMatcher: Matcher[_ >: U]): Matcher[T] = {
    TypeResolverFeatureMatcherFactoryForScala.feature(featureName, featureExtractor, featureMatcher)
  }

  def hasFeature[T, U](featureExtractor: (_ >: T) => _ <: U, featureMatcher: Matcher[_ >: U]): Matcher[T] = {
    TypeResolverFeatureMatcherFactoryForScala.feature(featureExtractor, featureMatcher)
  }

  def featureIterable[T, U](featureExtractor: (_ >: T) => _ <: U, iterableMatcher: Matcher[lang.Iterable[_ >: U]]): Matcher[Iterable[T]] = {
    val featureExtractor1: Iterable[_ <: T] => lang.Iterable[_ >: U] = iterable => asJava(iterable.map(featureExtractor))
    hasFeature(featureExtractor1, iterableMatcher)
  }

  def asJavaIterableMatcher[T](iterableMatcher: Matcher[lang.Iterable[_ >: T]]): Matcher[Iterable[_ <: T]] = {
    hasFeature(asJava, iterableMatcher)
  }
}
