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

package controllers

import javax.inject.Inject
import models._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import services.EligibilityService
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext

class EligibilityController @Inject()(
                                       service: EligibilityService
                                     )(implicit ec: ExecutionContext) extends BaseController {

  final def findEligibility: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[EligibilityRequest] { eligibilityRequest =>
      service.fetch(eligibilityRequest.nino, eligibilityRequest.taxYear) map {
        case Some(res) => Ok(Json.toJson(MarriageAllowanceEligibilitySummaryResponse(res.eligible)))
        case _ => NotFound(Json.toJson(ErrorNotFound))
      } recover fromFailure
    }
  }

  final def create(nino: Nino, taxYear: TaxYear): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[MarriageAllowanceEligibilityCreationRequest] { creationRequest =>
      for {
        result <- service.create(nino, taxYear.startYr, creationRequest.eligible)
      } yield Created(Json.toJson(MarriageAllowanceEligibilitySummaryResponse(result.eligible)))

    } recover {
      case _: NotFoundException =>
        NotFound(JsonErrorResponse("TEST_USER_NOT_FOUND", "No test individual exists with the specified National Insurance number"))
      case e  =>
        Logger.error("An error occurred while creating test data", e)
        InternalServerError
    }
  }

    private def fromFailure: PartialFunction[Throwable, Result] = {
      case _: NotFoundException => NotFound(Json.toJson(ErrorNotFound))
      case e: Throwable =>
        Logger.error("An error occurred while finding test data", e)
        InternalServerError(Json.toJson(ErrorInternalServerError))
  }
}
