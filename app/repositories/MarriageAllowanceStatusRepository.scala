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
class MarriageAllowanceStatusRepository @Inject()(
  mongoComponent: MongoComponent
)(implicit ec: ExecutionContext) extends PlayMongoRepository[StatusSummary](
  mongoComponent = mongoComponent,
  collectionName = "marriage-allowance-status",
  domainFormat = StatusSummary.format,
  indexes = Seq(
    IndexModel(
      Indexes.ascending("utr", "taxYear")
    )
  )
) {

  def store(marriageAllowanceStatusSummary: StatusSummary): Future[StatusSummary] =
    collection
      .insertOne(marriageAllowanceStatusSummary)
      .toFuture
      .map { _ => marriageAllowanceStatusSummary }

  def fetch(utr: String, taxYear: String): Future[Option[StatusSummary]] =
    collection
      .find(
        Filters.and(
          Filters.equal("utr", utr),
          Filters.equal("taxYear", taxYear)
        )
      )
      .toFuture
      .map {
        case Nil => None
        case statusSummary :: _ => Some(statusSummary)
      }
}
