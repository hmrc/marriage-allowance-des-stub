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
import play.api.libs.json.Json
import stubs.ApiPlatformTestUserStub
import uk.gov.hmrc.domain.Nino

class MarriageAllowanceEligibilitySpec extends IntegrationTest {

  override def beforeEach(): Unit = {
    super.beforeEach()
    ApiPlatformTestUserStub.willReturnTheIndividual(Nino("AC000003D"))
  }

  //TODO remove with other deprecated dependents
  feature("Fetch marriage allowance eligibility with get method") {
    scenario("Marriage allowance eligibility data is not returned for the given utr and taxYear as it hasn't been primed") {
      When("I fetch marriage allowance eligibility data for a given utr and taxYear")
      val fetchResponse = fetchMarriageAllowanceEligibilityGet("AB000003D", "Firstname", "Surname", "1980-01-01", "2014")

      Then("the response should not contain marriage allowance eligibility data")
      fetchResponse.code shouldBe NOT_FOUND
    }
  }

  feature("Fetch marriage allowance eligibility with post method") {
    scenario("Marriage allowance eligibility data is not returned for the given utr and taxYear as it hasn't been primed") {
      When("I fetch marriage allowance eligibility data for a given utr and taxYear")
      val fetchResponse = fetchMarriageAllowanceEligibilityPost("AB000003D", "Firstname", "Surname", "1980-01-01", "2014")

      Then("the response should not contain marriage allowance eligibility data")
      fetchResponse.code shouldBe NOT_FOUND
    }
  }

  //TODO remove with other deprecated dependents
  feature("Prime marriage allowance eligibility with get") {
    scenario("Marriage allowance eligibility data is returned for the given nine and taxYear when primed with the default scenario") {
      When("I prime marriage allowance eligibility data for a given utr and taxYear")
      val primeResponse = primeMarriageAllowanceEligibility("AC000003D", "2016-17", """{"eligible":true}""")

      Then("the response should indicate that marriage allowance eligibility data has been created")
      primeResponse.code shouldBe CREATED

      And("I request marriage allowance eligibility data for a given utr and taxYear")
      val fetchResponse = fetchMarriageAllowanceEligibilityGet("AC000003D", "Firstname", "Surname", "1980-01-01", "2016")

      And("The response should contain marriage allowance eligibility data")
      fetchResponse.code shouldBe OK

      fetchResponse.body shouldBe """{"eligible":true}"""

      (Json.parse(fetchResponse.body) \ "eligible").get.toString() shouldBe "true"
    }
  }

  feature("Prime marriage allowance eligibility with post") {
    scenario("Marriage allowance eligibility data is returned for the given nine and taxYear when primed with the default scenario") {
      When("I prime marriage allowance eligibility data for a given utr and taxYear")
      val primeResponse = primeMarriageAllowanceEligibility("AC000003D", "2016-17", """{"eligible":true}""")

      Then("the response should indicate that marriage allowance eligibility data has been created")
      primeResponse.code shouldBe CREATED

      And("I request marriage allowance eligibility data for a given utr and taxYear")
      val fetchResponse = fetchMarriageAllowanceEligibilityPost("AC000003D", "Firstname", "Surname", "1980-01-01", "2016")

      And("The response should contain marriage allowance eligibility data")
      fetchResponse.code shouldBe OK

      fetchResponse.body shouldBe """{"eligible":true}"""

      (Json.parse(fetchResponse.body) \ "eligible").get.toString() shouldBe "true"
    }
  }

  private def primeMarriageAllowanceEligibility(nino: String, taxYear: String, payload: String) =
    postEndpoint(s"nino/$nino/eligibility/$taxYear", payload)

  @Deprecated
  private def fetchMarriageAllowanceEligibilityGet(nino: String, firstname: String, surname: String, dateOfBirth: String, taxYearStart: String) =
    getEndpoint(s"marriage-allowance/citizen/$nino/eligibility?firstname=$firstname&surname=$surname&dateOfBirth=$dateOfBirth&taxYearStart=$taxYearStart")

  private def fetchMarriageAllowanceEligibilityPost(nino: String, firstname: String, surname: String, dateOfBirth: String, taxYearStart: String) =
    postEndpoint(s"marriage-allowance/citizen/$nino/eligibility?firstname=$firstname&surname=$surname&dateOfBirth=$dateOfBirth&taxYearStart=$taxYearStart")

}
