/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.libs.json.Json
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._

package object models {
  implicit val objectIdFormat = ReactiveMongoFormats.objectIdFormats
  implicit val marriageAllowanceStatusCreationRequest = Json.format[MarriageAllowanceStatusCreationRequest]
  implicit val marriageAllowanceStatusSummaryResponseFormat = Json.format[MarriageAllowanceStatusSummaryResponse]
  implicit val marriageAllowanceStatusSummaryFormat = Json.format[StatusSummary]
  implicit val marriageAllowanceEligibilityCreationRequest = Json.format[MarriageAllowanceEligibilityCreationRequest]
  implicit val marriageAllowanceEligibilitySummaryResponseFormat = Json.format[MarriageAllowanceEligibilitySummaryResponse]
  implicit val marriageAllowanceEligibilitySummaryFormat = Json.format[EligibilitySummary]

  implicit val apiAccessFormat = Json.format[APIAccess]

  implicit val individualDetailsFormat = Json.format[IndividualDetails]
  implicit val testIndividualFormat = Json.format[TestIndividual]
}
