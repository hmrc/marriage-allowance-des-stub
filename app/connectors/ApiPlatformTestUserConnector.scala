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

package connectors

import com.google.inject.Inject
import config.{ApplicationConfig, WSHttp}
import models.TestIndividual
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ApiPlatformTestUserConnector @Inject()(
                                              appConfig: ApplicationConfig,
                                              http: WSHttp
                                            )(implicit ec: ExecutionContext) {

  val serviceUrl: String = appConfig.apiTestUserUrl

  def fetchByNino(nino: Nino)(implicit hc: HeaderCarrier): Future[TestIndividual] = {
    http.GET[TestIndividual](s"$serviceUrl/individuals/nino/$nino")
  }
}
