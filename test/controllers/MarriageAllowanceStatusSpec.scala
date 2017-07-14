/*
 * Copyright 2017 HM Revenue & Customs
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

import models.MarriageAllowanceStatusSummary
import org.mockito.BDDMockito.given
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import services.MarriageAllowanceStatusService
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.play.filters.MicroserviceFilterSupport
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MarriageAllowanceStatusSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {
  trait Setup extends MicroserviceFilterSupport {
    val request = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
    implicit val headerCarrier = HeaderCarrier()

    val underTest = new MarriageAllowanceStatusController {
      override val service: MarriageAllowanceStatusService = mock[MarriageAllowanceStatusService]
    }

    val deceasedStatusSummary = MarriageAllowanceStatusSummary("utr", "2014", "status", true)
  }

  "fetch" should {

    "return the response when called with a utr and taxYear" in new Setup {

      given(underTest.service.fetch("utr", "2014")).willReturn(Future(Some(deceasedStatusSummary)))

      val result = await(underTest.find(SaUtr("utr"), "2014")(request))

      status(result) shouldBe Status.OK
      (jsonBodyOf(result) \ "deceased").get.toString() shouldBe "true"
    }
  }
}
