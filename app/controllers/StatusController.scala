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

package controllers

import javax.inject.Inject
import models.{MarriageAllowanceStatusCreationRequest, MarriageAllowanceStatusSummaryResponse, TaxYear}
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.StatusService
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

class StatusController @Inject()(
  service: StatusService,
  cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends BackendController(cc)
  with Logging {

  final def find(utr: SaUtr, taxYearStart: String): Action[AnyContent] = Action.async {
    service.fetch(utr.utr, taxYearStart) map {
      case Some(result) => Ok(Json.toJson(MarriageAllowanceStatusSummaryResponse(result.status, result.deceased)))
      case _ => NotFound
    } recover {
      case e =>
        logger.error("An error occurred while finding test data", e)
        InternalServerError
    }
  }

  final def create(utr: SaUtr, taxYear: TaxYear): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[MarriageAllowanceStatusCreationRequest] { createStatusRequest =>
      for {
        result <- service.create(utr.utr, taxYear.startYr, createStatusRequest.status, createStatusRequest.deceased)
      } yield Created(Json.toJson(MarriageAllowanceStatusSummaryResponse(result.status, result.deceased)))

    } recover {
      case e =>
        logger.error("An error occurred while creating test data", e)
        InternalServerError
    }
  }
}
