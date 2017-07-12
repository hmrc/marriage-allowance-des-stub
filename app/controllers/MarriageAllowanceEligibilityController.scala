/*
 * Copyright 2017 HM Revenue & Customs
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
import models._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import services.{MarriageAllowanceEligibilityService, MarriageAllowanceEligibilityServiceImpl, ScenarioLoader, ScenarioLoaderImpl}
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

trait MarriageAllowanceEligibilityController extends BaseController with StubResource {
  val scenarioLoader: ScenarioLoader
  val service: MarriageAllowanceEligibilityService

  final def find(utr: SaUtr, taxYear: TaxYear) = Action async {
    service.fetch(utr.utr, taxYear.startYr) map {
      case Some(result) => Ok(Json.toJson(result.response))
      case _ => NotFound
    } recover {
      case e =>
        Logger.error("An error occurred while finding test data", e)
        InternalServerError
    }
  }

  final def create(utr: SaUtr, taxYear: TaxYear) = Action.async(parse.json) { implicit request =>
    withJsonBody[MarriageAllowanceEligibilityCreationRequest] { createEligibilityRequest =>
      val scenario = createEligibilityRequest.scenario.getOrElse("HAPPY_PATH_1")

      for {
        scenario <- scenarioLoader.loadScenario[MarriageAllowanceEligibilitySummaryResponse]("marriage-allowance-eligibility", scenario)
        _ <- service.create(utr.utr, taxYear.startYr, scenario)
      } yield Created.as(JSON)

    } recover {
      case _: InvalidScenarioException  =>  BadRequest(JsonErrorResponse("UNKNOWN_SCENARIO", "Unknown test scenario"))
      case e                            =>
        Logger.error("An error occurred while creating test data", e)
        InternalServerError
    }
  }
}

final class MarriageAllowanceEligibilityControllerImpl @Inject()(override val scenarioLoader: ScenarioLoaderImpl,
                                                                 override val service: MarriageAllowanceEligibilityServiceImpl)
  extends MarriageAllowanceEligibilityController