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

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.play.filters.MicroserviceFilterSupport
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import util.ResourceLoader

class MarriageAllowanceStatusSpec extends UnitSpec with MockitoSugar with OneAppPerSuite with ResourceLoader {
  trait Setup extends MicroserviceFilterSupport {
    val request = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
    implicit val headerCarrier = HeaderCarrier()

    val underTest = new MarriageAllowanceStatus()
  }

  "fetch" should {

    "return the happy path response when called with a utr and taxYear" in new Setup {

      val expected = loadResource("/resources/marriage-allowance-status/happy_path.json")

      val result = await(underTest.fetch("11111", "2014-15")(request))

      status(result) shouldBe Status.OK
      jsonBodyOf(result) shouldBe Json.parse(expected)
    }
  }
}
