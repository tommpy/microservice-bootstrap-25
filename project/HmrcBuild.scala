import sbt.Keys._
import sbt._

object HmrcBuild extends Build {

  import uk.gov.hmrc.SbtAutoBuildPlugin
  import uk.gov.hmrc.versioning.SbtGitVersioning

  val appName = "microservice-bootstrap-25"

  val appDependencies = Dependencies.compile ++ Dependencies.test

  lazy val library = Project(appName, file("."))
    .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning)
    .settings(
      scalaVersion := "2.11.7",
      libraryDependencies ++= appDependencies,
      crossScalaVersions := Seq("2.11.7"),
      resolvers := Seq(
        Resolver.bintrayRepo("hmrc", "releases"),
        Resolver.bintrayRepo("hmrc", "release-candidates"),
        Resolver.typesafeRepo("releases")
      )
    )
    .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)

}

object Dependencies {

  import play.sbt.PlayImport._

  val compile = Seq(
    filters,
    "uk.gov.hmrc" %% "play-filters-25" % "0.1.0-2-g7394c24",
    "uk.gov.hmrc" %% "play-graphite-25" % "0.1.0-0-g0000000",
    "com.typesafe.play" %% "play" % "2.5.3",
    "com.kenshoo" %% "metrics-play" % "2.3.0_0.1.8"
  )

  val test = Seq(
    "com.typesafe.play" %% "play-test" % "2.5.3" % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.pegdown" % "pegdown" % "1.5.0" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test"
  )

}


