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

import com.google.inject.Inject
import javax.inject.Singleton
import models._
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.MongoComponent
import org.mongodb.scala.model.{Filters, IndexModel, Indexes}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MarriageAllowanceEligibilityRepository @Inject()(
  mongoComponent: MongoComponent
)(implicit ec: ExecutionContext) extends PlayMongoRepository[EligibilitySummary](
  mongoComponent = mongoComponent,
  collectionName = "marriage-allowance-eligibility",
  domainFormat = EligibilitySummary.format,
  indexes = Seq(
    IndexModel(
      Indexes.ascending("nino", "taxYearStart")
    )
  )
) {

  def store(summary: EligibilitySummary): Future[EligibilitySummary] =
    collection
      .insertOne(summary)
      .toFuture
      .map { _ => summary }

  def fetch(nino: String, taxYearStart: String): Future[Option[EligibilitySummary]] =
    collection
      .find(
        Filters.and(
          Filters.equal("nino", nino),
          Filters.equal("taxYearStart", taxYearStart)
        )
      )
      .toFuture
      .map {
        case Nil => None
        case summary :: _ => Some(summary)
      }
}
