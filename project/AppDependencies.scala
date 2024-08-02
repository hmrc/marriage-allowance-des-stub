import sbt._
import play.sbt.PlayImport.ws

object AppDependencies {

  private val scalatestVersion = "3.2.19"
  private val scalatestplusVersion = "7.0.1"
  private val flexmarkallVersion = "0.64.8"
  private val hmrcMongoVersion = "2.2.0"
  private val bootstrapVersion = "8.4.0"
  private val playSuffix = "play-30"

  lazy val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"          %% s"bootstrap-backend-$playSuffix" % bootstrapVersion,
    "uk.gov.hmrc"          %% s"domain-$playSuffix"            % "10.0.0",
    "uk.gov.hmrc.mongo"    %% s"hmrc-mongo-$playSuffix"        % hmrcMongoVersion
  )

  lazy val test: Seq[ModuleID] = Seq(
    "org.scalatest"                %% "scalatest"                    % scalatestVersion,
    "org.scalatestplus.play"       %% "scalatestplus-play"           % scalatestplusVersion,
    "org.mockito"                   % "mockito-core"                 % "5.12.0",
    "com.vladsch.flexmark"          % "flexmark-all"                 % flexmarkallVersion,
    "uk.gov.hmrc.mongo"            %% s"hmrc-mongo-test-$playSuffix" % hmrcMongoVersion,
    "uk.gov.hmrc"                  %% s"bootstrap-test-$playSuffix"  % bootstrapVersion,
    "org.scalaj"                   %% "scalaj-http"                  % "2.4.2",
    "org.wiremock"                  % "wiremock"                     % "3.9.1",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"         % "2.17.2"
  ).map(_ % "test")

  val all: Seq[ModuleID] = compile ++ test
}
