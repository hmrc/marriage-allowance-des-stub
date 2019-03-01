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

package connectors

import config.WSHttp
import models.TestIndividual
import play.api.{Configuration, Play}
import play.api.Mode.Mode
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.config.ServicesConfig
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

trait ApiPlatformTestUserConnector extends ServicesConfig {
  override protected def mode: Mode = Play.current.mode
  override protected def runModeConfiguration: Configuration = Play.current.configuration

  lazy val serviceUrl = baseUrl("api-platform-test-user")

  def fetchByNino(nino: Nino)(implicit hc: HeaderCarrier): Future[TestIndividual] = {
    WSHttp.GET[TestIndividual](s"$serviceUrl/individuals/nino/$nino")
  }
}

object ApiPlatformTestUserConnector extends ApiPlatformTestUserConnector