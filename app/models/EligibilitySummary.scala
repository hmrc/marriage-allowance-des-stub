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

import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class EligibilitySummary(
  nino: String,
  taxYearStart: String,
  firstname: String,
  surname: String,
  dateOfBirth: String,
  eligible: Boolean
)

object EligibilitySummary {

  implicit val format: Format[EligibilitySummary] = {
    ( (__ \ "nino").format[String]
    ~ (__ \ "taxYearStart").format[String]
    ~ (__ \ "firstname").format[String]
    ~ (__ \ "surname").format[String]
    ~ (__ \ "dateOfBirth").format[String]
    ~ (__ \ "eligible").format[Boolean]
    )(EligibilitySummary.apply, unlift(EligibilitySummary.unapply))
  }
}
