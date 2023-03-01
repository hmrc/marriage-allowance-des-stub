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

import models.{EligibilitySummary, TaxYear}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatestplus.play.PlaySpec
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.EligibilityService
import test.utils.MockitoMocking
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MarriageAllowanceEligibilitySpec extends PlaySpec with MockitoMocking {

  val mockElgibilityService: EligibilityService = mock[EligibilityService]

  trait Setup {
    val fetchRequest = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json").withBody[JsValue](Json.parse("""{"nino":"AA000003D", "firstname": "John", "surname": "Smith", "dateOfBirth": "1980-01-01", "taxYearStart": "2020-21"}"""))
    val createRequest = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json").withBody[JsValue](Json.parse("""{"eligible":true}"""))
    implicit val headerCarrier = HeaderCarrier()

    val underTest = new EligibilityController(mockElgibilityService, stubControllerComponents())

    val eligibleSummary = EligibilitySummary("nino", "2014", "firstname", "surname", "1980-01-31", true)
    val ineligibleSummary = EligibilitySummary("nino", "2014", "firstname", "surname", "1980-01-31", false)
  }

  "findEligibility" should {
    "return a success when the service successfully fetches the eligibility" in new Setup {
      given(mockElgibilityService.fetch(ArgumentMatchers.eq(Nino("AA000003D")), ArgumentMatchers.eq("2020-21"))).willReturn(Future.successful(Some(eligibleSummary)))

      val result = underTest.findEligibility(fetchRequest)

      status(result) mustBe Status.OK
      (contentAsJson(result) \ "eligible").get.toString() mustBe "true"
    }
    "return a not found when the service fails to fetch the eligibility" in new Setup {
      given(mockElgibilityService.fetch(ArgumentMatchers.eq(Nino("AA000003D")), ArgumentMatchers.eq("2020-21"))).willReturn(Future.successful(None))

      val result = underTest.findEligibility(fetchRequest)

      status(result) mustBe Status.NOT_FOUND
    }
    "return a not found when the service throws a NotFoundException" in new Setup {
      given(mockElgibilityService.fetch(ArgumentMatchers.eq(Nino("AA000003D")), ArgumentMatchers.eq("2020-21"))).willReturn(Future.failed(new NotFoundException("Not Found")))

      val result = underTest.findEligibility(fetchRequest)

      status(result) mustBe Status.NOT_FOUND
    }
    "return a internal server error when the service throws any other exception" in new Setup {
      given(mockElgibilityService.fetch(ArgumentMatchers.eq(Nino("AA000003D")), ArgumentMatchers.eq("2020-21"))).willReturn(Future.failed(new NullPointerException))

      val result = underTest.findEligibility(fetchRequest)

      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "create" should {
    "return a CREATED response when successful" in new Setup {

      given(mockElgibilityService.create(ArgumentMatchers.eq(Nino("AA000003D")), ArgumentMatchers.eq("2017"), ArgumentMatchers.eq(true))(any())).willReturn(Future.successful(eligibleSummary))

      val result = underTest.create(Nino("AA000003D"), TaxYear("2017-18"))(createRequest)

      status(result) mustBe Status.CREATED
      (contentAsJson(result) \ "eligible").get.toString() mustBe "true"
    }

    "return a TEST_USER_NOT_FOUND response when an unknown NINO is specified" in new Setup {

      given(mockElgibilityService.create(ArgumentMatchers.eq(Nino("AA000003D")), ArgumentMatchers.eq("2017"), ArgumentMatchers.eq(true))(any())).willReturn(Future.failed(new NotFoundException("Expected test error")))

      val result = underTest.create(Nino("AA000003D"), TaxYear("2017-18"))(createRequest)

      status(result) mustBe Status.NOT_FOUND
      contentAsJson(result) mustBe Json.parse(
        """{
          |  "code": "TEST_USER_NOT_FOUND",
          |  "message": "No test individual exists with the specified National Insurance number"
          |}""".stripMargin)
    }

    "return an INTERNAL_SERVER_ERROR response when the creation fails" in new Setup {

      given(mockElgibilityService.create(ArgumentMatchers.eq(Nino("AA000003D")), ArgumentMatchers.eq("2017"), ArgumentMatchers.eq(true))(any())).willReturn(Future.failed(new NullPointerException))

      val result = underTest.create(Nino("AA000003D"), TaxYear("2017-18"))(createRequest)

      status(result) mustBe INTERNAL_SERVER_ERROR
    }
  }
}
