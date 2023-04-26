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

import java.time.LocalDate
import connectors.ApiPlatformTestUserConnector
import models.{EligibilitySummary, IndividualDetails, TestIndividual}
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import repositories.MarriageAllowanceEligibilityRepository
import test.utils.MockitoMocking
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mongo.MongoComponent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EligibilityServiceSpec extends PlaySpec with MockitoMocking with BeforeAndAfterEach {

  val mockMongoComponent: MongoComponent = mock[MongoComponent]
  val mockConnector: ApiPlatformTestUserConnector = mock[ApiPlatformTestUserConnector]
  val mockEligibilityRepo: MarriageAllowanceEligibilityRepository = mock[MarriageAllowanceEligibilityRepository]

  class MockEligibilityService extends EligibilityService(mockEligibilityRepo, mockConnector)

  val testService: EligibilityService = new MockEligibilityService

  override def beforeEach(): Unit = {
    reset(mockEligibilityRepo)
  }

  "Eligibility Service" should {
    "return an eligibilityStatus when the creation is successful" in {
      implicit val hc: HeaderCarrier = new HeaderCarrier()

      val eligibilitySummary = EligibilitySummary("AA123456A", "2020-21", "John", "Smith", "2020-01-01", true)
      val testIndividual = TestIndividual(Nino("AA123456A"), IndividualDetails("John", "Smith", LocalDate.parse("2020-01-01")))

      when(mockConnector.fetchByNino(Nino("AA123456A"))).thenReturn(Future.successful(testIndividual))
      when(mockEligibilityRepo.store(eligibilitySummary)).thenReturn(Future.successful(eligibilitySummary))

      await(testService.create(Nino("AA123456A"), "2020-21", true))

      verify(mockEligibilityRepo, times(1)).store(eligibilitySummary)
    }
    "return an eligibilityStatus when it fetches a record" in {
      val eligibilitySummary = EligibilitySummary("AA123456A", "2020", "John", "Smith", "2020-01-01", true)
      when(mockEligibilityRepo.fetch("AA123456A", "2020")).thenReturn(Future.successful(Some(eligibilitySummary)))

      testService.fetch(Nino("AA123456A"), "2020-21")

      verify(mockEligibilityRepo, times(1)).fetch("AA123456A", "2020-21")

    }
  }
}
