import com.typesafe.sbt.SbtNativePackager.packageArchetype
import com.typesafe.sbt.packager.Keys._
import Dependencies._


play.Project.playScalaSettings

name := "play-fam"

version := "1.0"

packageArchetype.java_server

maintainer := "Gregory Bougeard <gbougeard@gmail.com>"

packageSummary := "play-FAM"

packageDescription := "play-FAM - Football Amateur Manager"

scalaVersion := "2.10.2"

resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)


libraryDependencies ++= Seq(
  play.Project.jdbc,
  play.Project.cache,
  play.Project.filters,
  slf4j,
  logbackClassic,
  mysql,
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.5",
  jodaMapper,
  playSlick,
  playMetrics,
  secureSocial,
  scalaCheck,
//  specs2,
  webjars,
  wjAngular,
  wjAngularStrap,
  wjAngularUi,
  wjAngularUiBootstrap,
  wjAngularUiCalendar,
  wjAngularUiUtils,
  wjBootstrap,
  wjPNtofify,
//  wjRestangular,
//  wjUnderscorejs,
  wjBootstrapDatepicker,
  wjBootstrapTimepicker,
  wjBootswatch,
  wjFamfamFlag,
  wjFontAwesome,
  wjIonic,
  wjJquery,
  wjJqueryUi,
  wjMomentjs,
  wjSelect2
)

play.Project.routesImport += "binders.Binders._"
