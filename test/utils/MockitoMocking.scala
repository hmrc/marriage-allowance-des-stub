/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.utils

import org.mockito.Mockito
import org.mockito.stubbing.Answer

import scala.reflect.ClassTag

trait MockitoMocking {

  def mock[T](implicit ev: ClassTag[T]): T =
    Mockito.mock(ev.runtimeClass.asInstanceOf[Class[T]])

  def mock[T](answer: Answer[Object])(implicit ev: ClassTag[T]): T =
    Mockito.mock(ev.runtimeClass.asInstanceOf[Class[T]], answer)
}