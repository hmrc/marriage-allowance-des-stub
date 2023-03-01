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

package controllers

import akka.stream.Materializer
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.Status.OK
import play.api.test.Helpers.{defaultAwaitTimeout, headers, status}
import play.api.test.{FakeRequest, Injecting}

class DocumentationControllerSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  val materializer: Materializer          = app.materializer

  lazy val controller: DocumentationController = inject[DocumentationController]

  "DocumentationController" must {
    "return OK status with application.raml in the body" in {
      val result = controller.raml("1.0", "application.raml")(FakeRequest("GET", "/api/conf/1.0/application.raml"))
      status(result) mustBe OK
    }
    "return a Json definition" in {
      val result = controller.definition(FakeRequest("GET", "/api/definition"))

      status(result) mustBe OK
      headers(result) must contain(CONTENT_TYPE -> "application/json;charset=utf-8")
    }
  }
}
