/*
 * Copyright 2021 HM Revenue & Customs
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

import models.{StatusSummary, TaxYear}
import org.mockito.BDDMockito.given
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.StatusService
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MarriageAllowanceStatusSpec extends PlaySpec with MockitoSugar {

  val mockStatusService: StatusService = mock[StatusService]
  trait Setup {
    val jsonBody = Json.parse(
      """
        |{
        |  "status": "Recipient",
        |  "deceased": true
        |}
      """.stripMargin)

    val fetchRequest = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
    val createRequest = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json").withBody(jsonBody)

    implicit val headerCarrier = HeaderCarrier()

    val underTest = new StatusController(mockStatusService, stubControllerComponents())

    val deceasedStatusSummary = StatusSummary("utr", "2014", "Recipient", true)
  }

  "fetch" should {

    "return the response when called with a utr and taxYear" in new Setup {

      given(mockStatusService.fetch("utr", "2014")).willReturn(Future(Some(deceasedStatusSummary)))

      val result = underTest.find(SaUtr("utr"), "2014")(fetchRequest)

      status(result) mustBe OK
      contentAsJson(result) mustBe jsonBody
    }
  }

  "create" should {

    "return a CREATED response with payload matching the request" in new Setup {

      given(mockStatusService.create("utr", "2014", "Recipient", true)).willReturn(Future(deceasedStatusSummary))

      val result = underTest.create(SaUtr("utr"), TaxYear("2014-15"))(createRequest)

      status(result) mustBe CREATED
      contentAsJson(result) mustBe jsonBody
    }
  }
}
