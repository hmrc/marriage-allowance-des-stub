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
import play.api.http.Status.{CREATED, NOT_FOUND, OK}

class MarriageAllowanceStatusSpec extends IntegrationTest {
  feature("Fetch marriage allowance status") {
    scenario("Marriage allowance status data is not returned for the given utr and taxYear as it hasn't been primed") {
      When("I fetch marriage allowance status data for a given utr and taxYear")
      val fetchResponse = fetchMarriageAllowanceStatus("3333333333", "2014")

      Then("the response should not contain marriage allowance status data")
      fetchResponse.code shouldBe NOT_FOUND
    }
  }

  feature("Prime marriage allowance status") {
    scenario("Marriage allowance status data is returned for the given utr and taxYear when primed with the default scenario") {
      When("I prime marriage allowance status data for a given utr and taxYear")
      val primeResponse = primeMarriageAllowanceStatus("1111111111", "2016-17", """{"status":"something","deceased":true}""")

      Then("the response should indicate that marriage allowance status data has been created")
      primeResponse.code shouldBe CREATED

      And("I request marriage allowance status data for a given utr and taxYear")
      val fetchResponse = fetchMarriageAllowanceStatus("1111111111", "2016")

      And("The response should contain marriage allowance status data")
      fetchResponse.code shouldBe OK
    }
  }

  private def primeMarriageAllowanceStatus(utr: String, taxYear: String, payload: String) =
    postEndpoint(s"sa/$utr/status/$taxYear", payload)

  private def fetchMarriageAllowanceStatus(utr: String, taxYearStart: String) =
    getEndpoint(s"marriage-allowance/individual/$utr/status?taxYearStart=$taxYearStart")
}
