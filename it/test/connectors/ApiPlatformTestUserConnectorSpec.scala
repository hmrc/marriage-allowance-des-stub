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

package connectors

import it.utils.UnitSpec
import models.{IndividualDetails, TestIndividual}

import java.time.LocalDate
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND}
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException, UpstreamErrorResponse}
import utils.WiremockHelper

class ApiPlatformTestUserConnectorSpec extends UnitSpec with GuiceOneAppPerSuite with WiremockHelper {

  override def fakeApplication(): Application = GuiceApplicationBuilder()
    .configure(
      "microservice.services.api-platform-test-user.port" -> server.port()
    ).build()

  val nino = Nino("WC885133C")
  val individual = TestIndividual(nino, IndividualDetails("Heather", "Ling", LocalDate.parse("1983-09-18")))

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    lazy val underTest: ApiPlatformTestUserConnector =
      app.injector.instanceOf[ApiPlatformTestUserConnector]
  }

  "fetchIndividual" should {
    "return the individual when a NINO that exists is specified" in new Setup {
      generateUserWithDefaultData(nino)

      val result = await(underTest.fetchByNino(nino))

      result shouldBe individual
    }

    "fail when the NINO does not exist" in new Setup {
      generateUserWithErrorStatus(NOT_FOUND)

      intercept[NotFoundException] {
        await(underTest.fetchByNino(nino))
      }
    }

    "fail when the remote service returns an error" in new Setup {
      generateUserWithErrorStatus(INTERNAL_SERVER_ERROR)

      intercept[UpstreamErrorResponse] {
        await(underTest.fetchByNino(nino))
      }
    }
  }
}
