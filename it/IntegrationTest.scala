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

import java.util.concurrent.TimeUnit

import org.scalatest._
import org.scalatestplus.play.OneServerPerSuite
import play.api.Application
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.duration.Duration
import scalaj.http.Http

trait IntegrationTest extends FeatureSpec with BeforeAndAfterAll with BeforeAndAfterEach with Matchers with OneServerPerSuite with GivenWhenThen {
  override lazy val port = 9000

  implicit override lazy val app: Application = GuiceApplicationBuilder().configure(
      "auditing.enabled" -> false,
      "auditing.traceRequests" -> false,
      "mongodb.uri" -> "mongodb://localhost:27017/marriage-allowance-des-stub",
      "run.mode" -> "It"
    ).build()

  val timeout = Duration(5, TimeUnit.SECONDS)
  val serviceUrl = s"http://localhost:$port"

  def getEndpoint(endpoint: String) =
    Http(s"$serviceUrl/$endpoint")
      .asString

  def postEndpoint(endpoint: String, payload: String) =
    Http(s"$serviceUrl/$endpoint")
      .method("POST")
      .header(HeaderNames.CONTENT_TYPE, "application/json")
      .header(HeaderNames.ACCEPT, "application/vnd.hmrc.1.0+json")
      .postData(payload)
      .asString
}
