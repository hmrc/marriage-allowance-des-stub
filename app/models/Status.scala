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

package models


import java.time.format.DateTimeFormatter
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate
import scala.util.Try

case class Status(status: String, deceased: Boolean)

case class Eligibility(eligible: Boolean)

object Eligibility {
  implicit val formats: OFormat[Eligibility] = Json.format[Eligibility]
}

case class EligibilityRequest(nino: Nino, firstname: String, surname: String, dateOfBirth: LocalDate, taxYear: String)

object EligibilityRequest extends ConstraintReads {

  val datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  implicit val localDateRead: Reads[LocalDate] = (json: JsValue) => json match {
    case JsString(dateOfBirth) if (Try(LocalDate.parse(dateOfBirth, datePattern)).isSuccess) =>
      JsSuccess(LocalDate.parse(dateOfBirth, datePattern))
    case _ => JsError("DOB_INVALID")
  }

  implicit val eligibilityRequestRead: Reads[EligibilityRequest] = (
    (JsPath \ "nino").read[Nino] and
      (JsPath \ "firstname").read[String] and
      (JsPath \ "surname").read[String] and
      (JsPath \ "dateOfBirth").read[LocalDate] and
      (JsPath \ "taxYearStart").read[String]
    )(EligibilityRequest.apply _)


  implicit val writes: Writes[EligibilityRequest] = Json.writes[EligibilityRequest]
}
