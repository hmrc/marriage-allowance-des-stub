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

package controllers

import javax.inject.Inject

import common.StubResource
import models.{MarriageAllowanceStatusCreationRequest, MarriageAllowanceStatusSummaryResponse, TaxYear}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Action
import services.{MarriageAllowanceStatusService, MarriageAllowanceStatusServiceImpl}
import uk.gov.hmrc.api.controllers.HeaderValidator
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

trait MarriageAllowanceStatusController extends BaseController with StubResource with HeaderValidator {
  val service: MarriageAllowanceStatusService

  final def find(utr: SaUtr, taxYearStart: String) = Action async {
    service.fetch(utr.utr, taxYearStart) map {
      case Some(result) => Ok(Json.toJson(MarriageAllowanceStatusSummaryResponse(result.status, result.deceased)))
      case _ => NotFound
    } recover {
      case e =>
        Logger.error("An error occurred while finding test data", e)
        InternalServerError
    }
  }

  final def create(utr: SaUtr, taxYear: TaxYear) = validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
    withJsonBody[MarriageAllowanceStatusCreationRequest] { createStatusRequest =>
      for {
        result <- service.create(utr.utr, taxYear.startYr, createStatusRequest.status, createStatusRequest.deceased)
      } yield Created(Json.toJson(MarriageAllowanceStatusSummaryResponse(result.status, result.deceased)))

    } recover {
      case e =>
        Logger.error("An error occurred while creating test data", e)
        InternalServerError
    }
  }
}

final class MarriageAllowanceStatusControllerImpl @Inject()(override val service: MarriageAllowanceStatusServiceImpl)
  extends MarriageAllowanceStatusController
