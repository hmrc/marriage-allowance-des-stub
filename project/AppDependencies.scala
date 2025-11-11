import sbt._
import play.sbt.PlayImport.ws

object AppDependencies {

  private val hmrcMongoVersion = "2.10.0"
  private val bootstrapVersion = "10.4.0"
  private val playSuffix = "play-30"

  lazy val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"          %% s"bootstrap-backend-$playSuffix" % bootstrapVersion,
    "uk.gov.hmrc"          %% s"domain-$playSuffix"            % "13.0.0",
    "uk.gov.hmrc.mongo"    %% s"hmrc-mongo-$playSuffix"        % hmrcMongoVersion
  )

  lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"            %% s"hmrc-mongo-test-$playSuffix" % hmrcMongoVersion,
    "uk.gov.hmrc"                  %% s"bootstrap-test-$playSuffix"  % bootstrapVersion
  ).map(_ % "test")

  val all: Seq[ModuleID] = compile ++ test
}
