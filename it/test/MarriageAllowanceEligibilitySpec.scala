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

import play.api.Application
import play.api.http.HeaderNames
import play.api.http.Status.{CREATED, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest}
import uk.gov.hmrc.domain.Nino
import utils.WiremockHelper

class MarriageAllowanceEligibilitySpec extends IntegrationTest with WiremockHelper {

  override def fakeApplication(): Application = GuiceApplicationBuilder().configure(
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false,
    "mongodb.uri" -> "mongodb://localhost:27017/marriage-allowance-des-stub",
    "microservice.services.api-platform-test-user.port" -> server.port(),
    "run.mode" -> "It"
  ).build()

  private val eligibleTrue = """{"eligible":true}"""
  def payload(nino: String, firstname:String, surname: String, dateOfBirth: String, taxYearStart: String) =
    s"""
       |{
       |"nino": "$nino",
       |"firstname": "$firstname",
       |"surname": "$surname",
       |"dateOfBirth": "$dateOfBirth",
       |"taxYearStart": "$taxYearStart"
       |}
       |""".stripMargin


  Feature("Fetch marriage allowance eligibility with post method") {
    Scenario("Fetch Marriage allowance eligibility data is not returned for the given utr and taxYear as it hasn't been primed") {
      When("I fetch marriage allowance eligibility data for a given utr and taxYear")
      val fetchRequest =
        FakeRequest(
          "POST",
          "/marriage-allowance/citizen/eligibility",
          FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")),
          payload("AB000003D", "Firstname", "Surname", "1980-01-01", "2014")
        )
      val fetchResponse = route(fakeApplication(), fetchRequest)

      Then("the response should not contain marriage allowance eligibility data")
      fetchResponse.map(status(_) shouldBe NOT_FOUND)
    }

    Scenario("Fetch Marriage allowance eligibility data returns 415 for missing header") {
      When("I fetch marriage allowance eligibility data for a given utr and taxYear with no Content-Type header")
      val fetchRequest =
        FakeRequest(
          "POST",
          "/marriage-allowance/citizen/eligibility",
          FakeHeaders(Seq()),
          payload("AB000003D", "Firstname", "Surname", "1980-01-01", "2014")
        )
      val fetchResponse = route(fakeApplication(), fetchRequest)

      Then("the response should not contain marriage allowance eligibility data")
      fetchResponse.map(status(_) shouldBe UNSUPPORTED_MEDIA_TYPE)
    }
  }
  

  Feature("Prime marriage allowance eligibility with post") {
    Scenario("Prime Marriage allowance eligibility data is returned for the given nino and taxYear when primed with the default scenario") {
      When("I prime marriage allowance eligibility data for a given utr and taxYear")
      generateUserWithDefaultData(Nino("AC000003D"))

      val createUserRequest = FakeRequest("POST", "/nino/AC000003D/eligibility/2016-17", FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), eligibleTrue)
      val primeResponse = route(fakeApplication(), createUserRequest)

      Then("the response should indicate that marriage allowance eligibility data has been created")
      primeResponse.map(status(_) shouldBe CREATED)

      And("I request marriage allowance eligibility data for a given utr and taxYear with post")
      val fetchRequest =
        FakeRequest(
          "POST",
          "/marriage-allowance/citizen/eligibility",
          FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")),
          payload("AC000003D", "Firstname", "Surname", "1980-01-01", "2016")
        )
      val fetchResponse = route(fakeApplication(), fetchRequest)

      And("The response should contain marriage allowance eligibility data")
      fetchResponse.map {
        response =>
          status(response) shouldBe OK
          contentAsString(response) shouldBe eligibleTrue
      }
    }

    Scenario("Prime Marriage allowance eligibility data is NotFound for the incorrect nino when primed with the default scenario") {
      When("I prime marriage allowance eligibility data for a given utr and taxYear")
      generateUserWithDefaultData(Nino("AC000003D"))

      val createUserRequest = FakeRequest("POST", "/nino/AC000003D/eligibility/2016-17", FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), eligibleTrue)
      val primeResponse = route(fakeApplication(), createUserRequest)

      Then("the response should indicate that marriage allowance eligibility data has been created")
      primeResponse.map(status(_) shouldBe CREATED)

      And("I request marriage allowance eligibility data with incorrect nino")
      val fetchRequest =
        FakeRequest(
          "POST",
          "/marriage-allowance/citizen/eligibility",
          FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")),
          payload("AB000003D", "Firstname", "Surname", "1980-01-01", "2016")
        )
      val fetchResponse = route(fakeApplication(), fetchRequest)

      And("The response should contain marriage allowance eligibility data")
      fetchResponse.map {
        response =>
          status(response) shouldBe NOT_FOUND
      }
    }

    Scenario("Prime Marriage allowance eligibility data is NotFound for the incorrect taxyear when primed with the default scenario") {
      When("I prime marriage allowance eligibility data for a given utr and taxYear")
      generateUserWithDefaultData(Nino("AC000003D"))

      val createUserRequest = FakeRequest("POST", "/nino/AC000003D/eligibility/2016-17", FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), eligibleTrue)
      val primeResponse = route(fakeApplication(), createUserRequest)

      Then("the response should indicate that marriage allowance eligibility data has been created")
      primeResponse.map(status(_) shouldBe CREATED)

      And("I request marriage allowance eligibility data with incorrect taxyear")
      val fetchRequest =
        FakeRequest(
          "POST",
          "/marriage-allowance/citizen/eligibility",
          FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")),
          payload("AC000003D", "Firstname", "Surname", "1980-01-01", "2014")
        )
      val fetchResponse = route(fakeApplication(), fetchRequest)

      And("The response should contain marriage allowance eligibility data")
      fetchResponse.map {
        response =>
          status(response) shouldBe NOT_FOUND
      }
    }
  }
}
