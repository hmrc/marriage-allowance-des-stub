import sbt._
import play.sbt.PlayImport.ws

object AppDependencies {

  lazy val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"          %% "bootstrap-backend-play-28" % "5.14.0",
    "uk.gov.hmrc"          %% "domain"                    % "6.2.0-play-28",
    "uk.gov.hmrc"          %% "simple-reactivemongo"      % "8.0.0-play-26",
  )

  lazy val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0",
    "uk.gov.hmrc"            %% "reactivemongo-test" % "5.0.0-play-28",
    "org.mockito"             % "mockito-core"       % "3.12.4",
    "com.vladsch.flexmark"    % "flexmark-all"       % "0.36.8"
  ).map(_ % "test")

  val itTest: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"       %% "scalatestplus-play"   % "5.1.0",
    "org.scalaj"                   %% "scalaj-http"          % "2.4.2",
    "com.github.tomakehurst"        % "wiremock-jre8"        % "2.31.0",
    "com.vladsch.flexmark"          % "flexmark-all"         % "0.36.8",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.5"
  ).map(_ % "it")

  val all: Seq[ModuleID] = compile ++ test ++ itTest
}
