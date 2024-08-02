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

import play.api.http.Status.{CREATED, NOT_FOUND, OK}
import play.api.libs.json.{JsBoolean, JsObject, JsString}
import play.api.test.{FakeHeaders, FakeRequest}
import play.api.test.Helpers._
import utils.WiremockHelper

class MarriageAllowanceStatusSpec extends IntegrationTest with WiremockHelper {
  Feature("Fetch marriage allowance status") {

    Scenario("Marriage allowance status data is not returned for the given utr and taxYear as it hasn't been primed") {
      When("I fetch marriage allowance status data for a given utr and taxYear")
      val request = FakeRequest("POST", "/marriage-allowance/individual/3333333333/status?taxYearStart=2014")
      val fetchResponse = route(fakeApplication(), request)

      Then("the response should not contain marriage allowance status data")
      fetchResponse.map(status(_) shouldBe NOT_FOUND)
    }
  }

  Feature("Prime marriage allowance status") {
    Scenario("Marriage allowance status data is returned for the given utr and taxYear when primed with the default scenario") {
      When("I prime marriage allowance status data for a given utr and taxYear")
      val requestBody = JsObject(Map("status" -> JsString("something"), "deceased" -> JsBoolean(true)))
      val request = FakeRequest("POST", "/sa/1111111111/status/2016-17", FakeHeaders(Seq()), requestBody)
      val primeResponse = route(fakeApplication(), request)
      Then("the response should indicate that marriage allowance status data has been created")
      primeResponse.map(status(_) shouldBe CREATED)

      And("I request marriage allowance status data for a given utr and taxYear")
      val fetchRequest = FakeRequest("GET", "/marriage-allowance/individual/1111111111/status?taxYearStart=2016")
      val fetchResponse = route(fakeApplication(), fetchRequest)

      And("The response should contain marriage allowance status data")
      fetchResponse.map(status(_) shouldBe OK)
    }
  }
}
