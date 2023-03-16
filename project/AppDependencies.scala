import sbt._
import play.sbt.PlayImport.ws

object AppDependencies {

  val scalatestVersion = "3.2.14"
  val scalatestplusVersion = "5.1.0"
  val flexmarkallVersion = "0.62.2"
  val hmrcMongoVersion = "1.1.0"
  val bootstrapVersion = "7.14.0"

  lazy val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"          %% "bootstrap-backend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"          %% "domain"                    % "8.1.0-play-28",
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-play-28"        % hmrcMongoVersion
  )

  lazy val test: Seq[ModuleID] = Seq(
    "org.scalatest"          %% "scalatest"               % scalatestVersion,
    "org.scalatestplus.play" %% "scalatestplus-play"      % scalatestplusVersion,
    "org.mockito"             % "mockito-core"            % "4.8.1",
    "com.vladsch.flexmark"    % "flexmark-all"            % flexmarkallVersion,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % hmrcMongoVersion,
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapVersion,
  ).map(_ % "test")

  val itTest: Seq[ModuleID] = Seq(
    "org.scalatest"                %% "scalatest"            % scalatestVersion,
    "org.scalatestplus.play"       %% "scalatestplus-play"   % scalatestplusVersion,
    "org.scalaj"                   %% "scalaj-http"          % "2.4.2",
    "com.github.tomakehurst"        % "wiremock-jre8"        % "2.34.0",
    "com.vladsch.flexmark"          % "flexmark-all"         % flexmarkallVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.4",
    "uk.gov.hmrc"                  %% "bootstrap-test-play-28" % bootstrapVersion,
  ).map(_ % "it")

  val all: Seq[ModuleID] = compile ++ test ++ itTest
}
