import sbt._
import play.sbt.PlayImport.ws

object AppDependencies {

  val hmrcTestVersion = "3.9.0-play-26"
  val scalaTestPlusPlayVersion = "3.1.3"

  lazy val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % "2.1.0",
    "uk.gov.hmrc" %% "domain" % "5.10.0-play-26",
    "uk.gov.hmrc" %% "simple-reactivemongo" % "8.0.0-play-26",
    "com.typesafe.play" %% "play-json-joda" % "2.7.4"
  )

  lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion,
    "uk.gov.hmrc" %% "reactivemongo-test" % "4.22.0-play-26",
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion,
    "org.mockito" % "mockito-core" % "3.6.28"
  ).map(_ % "test")

  val itTest: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion,
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion,
    "org.scalaj" %% "scalaj-http" % "2.4.1",
    "com.github.tomakehurst" % "wiremock-jre8" % "2.26.1"
  ).map(_ % "it") //TODO refactor

  val all: Seq[ModuleID] = compile ++ test ++ itTest
}
