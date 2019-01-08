/*
 * Copyright 2019 HM Revenue & Customs
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

import models._
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DB
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MarriageAllowanceStatusRepository extends Repository[MarriageAllowanceStatusSummary, BSONObjectID] {
  def store[T <: MarriageAllowanceStatusSummary](marriageAllowanceStatusSummary: T): Future[T]
  def fetch(utr: String, taxYear: String): Future[Option[MarriageAllowanceStatusSummary]]
}

object MarriageAllowanceStatusRepository extends MongoDbConnection {
  private lazy val repository = new MarriageAllowanceStatusMongoRepository
  def apply(): MarriageAllowanceStatusRepository = repository
}

class MarriageAllowanceStatusMongoRepository(implicit mongo: () => DB) extends ReactiveRepository[MarriageAllowanceStatusSummary, BSONObjectID]("marriage-allowance-status", mongo,
  marriageAllowanceStatusSummaryFormat, objectIdFormat) with MarriageAllowanceStatusRepository {
  override def store[T <: MarriageAllowanceStatusSummary](marriageAllowanceStatusSummary: T): Future[T] =
    for{
      _ <- remove("utr" -> marriageAllowanceStatusSummary.utr, "taxYear" -> marriageAllowanceStatusSummary.taxYear)
      _ <- insert(marriageAllowanceStatusSummary)
    } yield marriageAllowanceStatusSummary

  override def fetch(utr: String, taxYear: String): Future[Option[MarriageAllowanceStatusSummary]] =
    find("utr" -> utr, "taxYear" -> taxYear) map(_.headOption)
}
