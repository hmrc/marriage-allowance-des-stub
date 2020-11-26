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

package config

import akka.actor.ActorSystem
import com.google.inject.{ImplementedBy, Inject}
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import play.api.Mode.Mode
import play.api.Play.current
import play.api._
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.hooks.{HttpHook, HttpHooks}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.auth.controllers.AuthParamsControllerConfig
import uk.gov.hmrc.play.config.{AppName, ControllerConfig, RunMode}
import uk.gov.hmrc.play.http.ws._
import uk.gov.hmrc.play.microservice.bootstrap.DefaultMicroserviceGlobal
import uk.gov.hmrc.play.microservice.config.LoadAuditingConfig
import uk.gov.hmrc.play.microservice.filters.{AuditFilter, LoggingFilter, MicroserviceFilterSupport}

trait Hooks extends HttpHooks { //TODO should this have auditing hook?
  override val hooks: Seq[HttpHook] = NoneRequired
}

@ImplementedBy(classOf[WSHttpImpl])
trait WSHttp extends HttpGet with WSGet with HttpPut with WSPut with HttpPost with WSPost with HttpDelete with WSDelete

class WSHttpImpl @Inject()(
                        config: Configuration,
                        val actorSystem: ActorSystem
                      ) extends WSHttp with Hooks with AppName {
  override protected def appNameConfiguration: Configuration = config
  override protected def configuration: Option[Config] = Some(config.underlying)
}

object ControllerConfiguration extends ControllerConfig {
  lazy val controllerConfigs = current.configuration.underlying.as[Config]("controllers")
}

object MicroserviceAuditConnector extends AuditConnector {
  override lazy val auditingConfig = LoadAuditingConfig("auditing")
}

object MicroserviceAuditFilter extends AuditFilter with AppName with MicroserviceFilterSupport {
  override protected def appNameConfiguration: Configuration = current.configuration

  override val auditConnector = MicroserviceAuditConnector

  override def controllerNeedsAuditing(controllerName: String) = ControllerConfiguration.paramsForController(controllerName).needsAuditing
}

object MicroserviceLoggingFilter extends LoggingFilter with MicroserviceFilterSupport {
  override def controllerNeedsLogging(controllerName: String) = ControllerConfiguration.paramsForController(controllerName).needsLogging
}

object StubGlobal extends DefaultMicroserviceGlobal with RunMode with MicroserviceFilterSupport {

  override protected def mode: Mode = current.mode
  override protected def runModeConfiguration: Configuration = current.configuration

  override val loggingFilter = MicroserviceLoggingFilter
  override val microserviceAuditFilter = MicroserviceAuditFilter
  override val auditConnector = MicroserviceAuditConnector
  override val authFilter = None
  
  override def microserviceMetricsConfig(implicit app: Application) = app.configuration.getConfig("microservice.metrics")

}