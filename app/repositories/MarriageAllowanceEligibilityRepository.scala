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

package repositories

import com.google.inject.Provider
import models._
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DB
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MarriageAllowanceEligibilityRepository extends Repository[EligibilitySummary, BSONObjectID] {
  def store[T <: EligibilitySummary](marriageAllowanceEligibilitySummary: T): Future[T]
  def fetch(nino: String, taxYearStart: String): Future[Option[EligibilitySummary]]
}

class EligibilityRepositoryProvider extends Provider[MarriageAllowanceEligibilityRepository] {
  override def get(): MarriageAllowanceEligibilityRepository = MarriageAllowanceEligibilityRepository()
}

object MarriageAllowanceEligibilityRepository extends MongoDbConnection {
  private lazy val repository = new EligibilityRepository
  def apply(): MarriageAllowanceEligibilityRepository = repository
}

class EligibilityRepository(implicit mongo: () => DB) extends ReactiveRepository[EligibilitySummary, BSONObjectID]("marriage-allowance-eligibility", mongo,
  marriageAllowanceEligibilitySummaryFormat, objectIdFormat) with MarriageAllowanceEligibilityRepository {
  override def store[T <: EligibilitySummary](marriageAllowanceEligibilitySummary: T): Future[T] =
    for{
      _ <- remove("nino" -> marriageAllowanceEligibilitySummary.nino, "taxYearStart" -> marriageAllowanceEligibilitySummary.taxYearStart)
      _ <- insert(marriageAllowanceEligibilitySummary)
    } yield marriageAllowanceEligibilitySummary

  override def fetch(nino: String, taxYearStart: String): Future[Option[EligibilitySummary]] = find("nino" -> nino, "taxYearStart" -> taxYearStart) map(_.headOption)
}
