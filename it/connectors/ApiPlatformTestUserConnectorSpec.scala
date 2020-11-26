/*
 * Copyright 2020 HM Revenue & Customs
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

package connectors

import models.{IndividualDetails, TestIndividual}
import org.joda.time.LocalDate
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import stubs.ApiPlatformTestUserStub
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException, Upstream5xxResponse}
import uk.gov.hmrc.play.test.UnitSpec

class ApiPlatformTestUserConnectorSpec extends UnitSpec with BeforeAndAfterEach with BeforeAndAfterAll with GuiceOneAppPerSuite {

  override def fakeApplication(): Application = GuiceApplicationBuilder()
    .configure(
      "microservice.services.api-platform-test-user.port" -> ApiPlatformTestUserStub.port
    ).build()

  val nino = Nino("WC885133C")
  val individual = TestIndividual(nino, IndividualDetails("Heather", "Ling", LocalDate.parse("1983-09-18")))

  trait Setup {
    implicit val hc = HeaderCarrier()

    val underTest: ApiPlatformTestUserConnector =
      app.injector.instanceOf[ApiPlatformTestUserConnector]
  }

  override def beforeAll() = {
    super.beforeAll()
    ApiPlatformTestUserStub.server.start()
  }

  override def beforeEach() = {
    super.beforeEach()
    ApiPlatformTestUserStub.server.resetMappings()
  }

  override def afterAll() = {
    super.afterAll()
    ApiPlatformTestUserStub.server.stop()
  }

  "fetchIndividual" should {
    "return the individual when a NINO that exists is specified" in new Setup {
      ApiPlatformTestUserStub.willReturnTheIndividual(nino)

      val result = await(underTest.fetchByNino(nino))

      result shouldBe individual
    }

    "fail when the NINO does not exist" in new Setup {
      ApiPlatformTestUserStub.willNotFindTheIndividual()

      intercept[NotFoundException] {
        await(underTest.fetchByNino(nino))
      }
    }

    "fail when the remote service returns an error" in new Setup {
      ApiPlatformTestUserStub.willReturnAnError()

      intercept[Upstream5xxResponse] {
        await(underTest.fetchByNino(nino))
      }
    }
  }
}
