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

package controllers

import models.{EligibilitySummary, TaxYear}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import services.EligibilityService
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException}
import uk.gov.hmrc.play.microservice.filters.MicroserviceFilterSupport

class MarriageAllowanceEligibilitySpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  trait Setup extends MicroserviceFilterSupport {
    val fetchRequest = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
    val createRequest = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json").withBody[JsValue](Json.parse("""{"eligible":true}"""))
    implicit val headerCarrier = HeaderCarrier()

    val underTest = new EligibilityController {
      override val service: EligibilityService = mock[EligibilityService]
    }

    val eligibleSummary = EligibilitySummary("nino", "2014", "firstname", "surname", "1980-01-31", true)
    val ineligibleSummary = EligibilitySummary("nino", "2014", "firstname", "surname", "1980-01-31", false)
  }

  "create" should {
    "return a CREATED response when successful" in new Setup {

      given(underTest.service.create(ArgumentMatchers.eq(Nino("AA000003D")), ArgumentMatchers.eq("2017"), ArgumentMatchers.eq(true))(any())).willReturn(Future.successful(eligibleSummary))

      val result = await(underTest.create(Nino("AA000003D"), TaxYear("2017-18"))(createRequest))

      status(result) shouldBe Status.CREATED
      (jsonBodyOf(result) \ "eligible").get.toString() shouldBe "true"
    }

    "return a TEST_USER_NOT_FOUND response when an unknown NINO is specified" in new Setup {

      given(underTest.service.create(ArgumentMatchers.eq(Nino("AA000003D")), ArgumentMatchers.eq("2017"), ArgumentMatchers.eq(true))(any())).willReturn(Future.failed(new NotFoundException("Expected test error")))

      val result = await(underTest.create(Nino("AA000003D"), TaxYear("2017-18"))(createRequest))

      status(result) shouldBe Status.NOT_FOUND
      jsonBodyOf(result) shouldBe Json.parse(
        """{
          |  "code": "TEST_USER_NOT_FOUND",
          |  "message": "No test individual exists with the specified National Insurance number"
          |}""".stripMargin)
    }
  }
}
