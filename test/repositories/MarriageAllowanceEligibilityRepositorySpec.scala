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

package repositories

import models.EligibilitySummary
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class MarriageAllowanceEligibilityRepositorySpec extends PlaySpec with DefaultPlayMongoRepositorySupport[EligibilitySummary] {
  override lazy val repository = new MarriageAllowanceEligibilityRepository(mongoComponent)

  "MarriageAllowanceEligibilityRepository" must {
    "return an eligibility summary" when {
      "storing the eligibility summary and then fetching it" in {
        val eligibilitySummary = EligibilitySummary("nino", "2021-22", "john", "smith", "19-03-75", true)
        val storeResult = await(repository.store(eligibilitySummary))

        storeResult shouldBe eligibilitySummary
        val fetchResult = await(repository.fetch("nino", "2021-22"))

        fetchResult.isEmpty shouldBe false
        fetchResult.get shouldBe eligibilitySummary
      }
    }
    "return nothing" when {
      "there is no status summary that matches in the database" in {
        val result = await(repository.fetch("nino", "2021-22"))

        result.isEmpty shouldBe true
      }
    }
  }
}
