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

package services

import models.StatusSummary
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import repositories.MarriageAllowanceStatusRepository
import test.utils.MockitoMocking
import uk.gov.hmrc.mongo.MongoComponent

import scala.concurrent.Future

class StatusServiceSpec extends PlaySpec with MockitoMocking with BeforeAndAfterEach {

  val mockMongoComponent: MongoComponent = mock[MongoComponent]
  val mockStatusRepo: MarriageAllowanceStatusRepository = mock[MarriageAllowanceStatusRepository]

  class MockStatusService extends StatusService(mockStatusRepo)

  val testService: StatusService = new MockStatusService

  override def beforeEach: Unit = {
    reset(mockStatusRepo)
  }

  "Status Service" should {
    "return a statusSummary when the creation is successful" in {
      val statusSummary = StatusSummary("utr", "2020-21", "status", true)
      when(mockStatusRepo.store(statusSummary)).thenReturn(Future.successful(statusSummary))

      testService.create("utr", "2020-21", "status", true)

      verify(mockStatusRepo, times(1)).store(statusSummary)

    }
    "return a statusSummary when it fetches a record" in {
      val statusSummary = StatusSummary("utr", "2020-21", "status", true)
      when(mockStatusRepo.fetch("utr", "2020-21")).thenReturn(Future.successful(Some(statusSummary)))

      testService.fetch("utr", "2020-21")

      verify(mockStatusRepo, times(1)).fetch("utr", "2020-21")

    }
  }
}
