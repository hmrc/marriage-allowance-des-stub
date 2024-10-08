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

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import controllers.DocumentationController
import it.utils.UnitSpec
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterEach, TestData}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.{Application, Mode}

import scala.concurrent.Future

/**
  * Testcase to verify the capability of integration with the API platform.
  *
  * 1, To integrate with API platform the service needs to register itself to the service locator by calling the /registration endpoint and providing
  * - application name
  * - application url
  *
  * 2a, To expose API's to Third Party Developers, the service needs to make the API definition available under api/definition GET endpoint
  * 2b, The endpoints need to be defined in an application.yaml file for all versions  For all of the endpoints defined documentation will be provided and be available under api/documentation/[version]/[endpoint name] GET endpoint
  * Example: api/documentation/1.0/Fetch-Some-Data
  */
class PlatformIntegrationSpec extends UnitSpec with ScalaFutures with BeforeAndAfterEach with GuiceOneAppPerTest {

  val stubHost = "localhost"
  val stubPort = 11111
  val wireMockServer = new WireMockServer(wireMockConfig().port(stubPort))

  override def newAppForTest(testData: TestData): Application = GuiceApplicationBuilder()
    .configure("run.mode" -> "Stub")
    .configure(Map(
      "appName" -> "application-name",
      "appUrl" -> "http://microservice-name.protected.mdtp"
    )).in(Mode.Test).build()

  override def beforeEach(): Unit = {
    wireMockServer.start()
    WireMock.configureFor(stubHost, stubPort)
    stubFor(post(urlMatching("/registration")).willReturn(aResponse().withStatus(204)))
  }

  trait Setup {
    val documentationController: DocumentationController = app.injector.instanceOf[DocumentationController]
    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  }

  "microservice" should {

    "provide definition endpoint" in new Setup {
      val result: Future[Result] = documentationController.definition()(request)
      status(result) shouldBe 200
    }

    "provide OAS conf endpoint" in new Setup {
      val result: Future[Result] = documentationController.yaml("1.0", "application.yaml")(request)
      status(result) shouldBe 200
    }
  }

  override protected def afterEach(): Unit = {
    wireMockServer.stop()
    wireMockServer.resetMappings()
  }
}
