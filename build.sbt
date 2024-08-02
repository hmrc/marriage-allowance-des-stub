import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin

lazy val appName = "marriage-allowance-des-stub"


ThisBuild / scalaVersion := "2.13.14"
ThisBuild / majorVersion := 0

lazy val microservice = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(
    scalaSettings,
    defaultSettings(),
    routesImport += "controllers.Binders._",
    name := appName,
    libraryDependencies ++= AppDependencies.all,
    retrieveManaged := true,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
    resolvers ++= Seq(
      Resolver.jcenterRepo
    )
  ).settings(
  scalacOptions ++= Seq(
    "-feature",
    "-Werror",
    "-Wconf:cat=unused-imports&site=.*views\\.html.*:s",
    "-Wconf:cat=unused-imports&site=.*views\\.txt.*:s",
    "-Wconf:cat=unused-imports&site=<empty>:s",
    "-Wconf:cat=unused&src=.*RoutesPrefix\\.scala:s",
    "-Wconf:cat=unused&src=.*Routes\\.scala:s",
    "-Wconf:cat=unused&src=.*ReverseRoutes\\.scala:s",
    "-Wconf:cat=unused&src=.*JavaScriptReverseRoutes\\.scala:s"
  ),
)
// Coverage configuration
coverageMinimumStmtTotal := 17.27
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo;.*Routes.*;.*config.*;"

val it: Project = project.in(file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings())