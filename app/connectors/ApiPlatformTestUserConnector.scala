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

package connectors

import com.google.inject.Inject
import config.ApplicationConfig
import models.TestIndividual
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.HttpReadsInstances.readEitherOf
import uk.gov.hmrc.http.{NotFoundException, _}

import scala.concurrent.{ExecutionContext, Future}

class ApiPlatformTestUserConnector @Inject()(
                                              appConfig: ApplicationConfig,
                                              http: HttpClient
                                            )(implicit ec: ExecutionContext) {

  val serviceUrl: String = appConfig.apiTestUserUrl

  def fetchByNino(nino: Nino)(implicit hc: HeaderCarrier): Future[TestIndividual] = {
    http
      .GET[Either[UpstreamErrorResponse, HttpResponse]](s"$serviceUrl/individuals/nino/$nino")
      .map {
        case Right(response) => response.json.as[TestIndividual]
        case Left(error) => {
          if (error.statusCode == NOT_FOUND) throw new NotFoundException(error.message)
          else throw error
        }
      }
  }
}
