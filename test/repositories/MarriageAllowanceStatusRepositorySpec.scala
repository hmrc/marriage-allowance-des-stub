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

import models.StatusSummary
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class MarriageAllowanceStatusRepositorySpec extends PlaySpec with DefaultPlayMongoRepositorySupport[StatusSummary] {
  override lazy val repository = new MarriageAllowanceStatusRepository(mongoComponent)

  "MarriageAllowanceStatusRepository" must {
    "return a status summary" when {
      "storing the status summary and then fetching it" in {
        val statusSummary = StatusSummary("utr1", "2021-22", "true", false)
        val storeResult = await(repository.store(statusSummary))

        storeResult shouldBe statusSummary
        val fetchResult = await(repository.fetch("utr1", "2021-22"))

        fetchResult.isEmpty shouldBe false
        fetchResult.get shouldBe statusSummary
      }
    }
    "return nothing" when {
      "there is no status summary that matches in the database" in {
        val result = await(repository.fetch("utr1", "2021-22"))

        result.isEmpty shouldBe true
      }
    }
  }
}
