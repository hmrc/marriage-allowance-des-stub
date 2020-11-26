/*
 * Copyright 2020 HM Revenue & Customs
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

import com.google.inject.Inject
import connectors.ApiPlatformTestUserConnector
import models.EligibilitySummary
import repositories.MarriageAllowanceEligibilityRepository
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class EligibilityService @Inject()(
                                    repository: MarriageAllowanceEligibilityRepository,
                                    testUserConnector: ApiPlatformTestUserConnector
                                  )(implicit ec: ExecutionContext) {

  def create(nino: Nino, taxYearStart: String, eligible: Boolean)(implicit hc: HeaderCarrier): Future[EligibilitySummary] = {
    for {
      individualDetails <- testUserConnector.fetchByNino(nino).map(_.individualDetails)
      eligibilitySummary = EligibilitySummary(nino.nino, taxYearStart, individualDetails.firstName,
        individualDetails.lastName, individualDetails.dateOfBirth.toString, eligible)
      _ <- repository.store(eligibilitySummary)
    } yield eligibilitySummary
  }

  def fetch(nino: Nino, taxYearStart: String): Future[Option[EligibilitySummary]] = {
    repository.fetch(nino.nino, taxYearStart)
  }
}
