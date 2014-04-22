import sbt._
import Keys._

object Dependencies {
  // Versions
  val akkaVersion = "2.2.2"
  val logbackVersion = "1.1.1"
  val slf4jVersion = "1.7.6"
  val mysqlVersion = "5.1.30"
  val jodaMapperVersion = "1.0.1"
  val playSlickVersion = "0.6.0.1"
  val secureSocialVersion = "2.1.2"
  val playMetricsVersion = "0.1.3"

  val scalaCheckVersion = "1.10.1"
  val specs2Version = "2.3.11"
  val scalazVersion = "7.0.4"

  val webjarsVersion = "2.2.1-2"
  val wjAngularVersion = "1.2.16"
  val wjAngularMotionVersion = "0.3.2"
  val wjAngularUiVersion = "0.4.0-2"
  val wjAngularUiBootstrapVersion = "0.10.0-1"
  val wjAngularUiCalendarVersion = "0.8.0"
  val wjAngularUiUtilsVersion = "0.1.0-1"
  val wjAngularStrapVersion = "2.0.0"
  val wjBootstrapVersion = "3.1.1"
  val wjBootswatchVersion = "3.1.1"
  val wjBsDatepickerVersion = "1.3.0-1"
  val wjBsTimepickerVersion = "0.2.3"
  val wjIonicVersion = "0.9.24"
  val wjPNotifyVersion = "1.2.0"
  //  val wjCodeMirrorVersion = "3.21"
  val wjFontAwesomeVersion = "4.0.3"
  val wjMomentjsVersion = "2.5.1-1"
  val wjSelect2Version = "3.4.6"
  val wjJqueryVersion = "1.11.0"
  val wjJqueryUiVersion = "1.10.3"

  // Libraries
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion
  val slf4j = "org.slf4j" % "slf4j-api" % slf4jVersion
  val mysql = "mysql" % "mysql-connector-java" % mysqlVersion
  val jodaMapper = "com.github.tototoshi" %% "slick-joda-mapper" % jodaMapperVersion
  val playSlick = "com.typesafe.play" %% "play-slick" % playSlickVersion
  val playMetrics = "com.kenshoo" %% "metrics-play" % playMetricsVersion
  val secureSocial = "securesocial" %% "securesocial" % secureSocialVersion
  val scalaCheck = "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  val specs2 = "org.specs2" %% "specs2" % specs2Version % "test"

  val webjars = "org.webjars" % "webjars-play_2.10" % webjarsVersion
  val wjAngular = "org.webjars" % "angularjs" % wjAngularVersion
  val wjAngularMotion = "org.webjars" % "angular-motion" % wjAngularMotionVersion
  val wjAngularUi = "org.webjars" % "angular-ui" % wjAngularUiVersion
  val wjAngularUiBootstrap = "org.webjars" % "angular-ui-bootstrap" % wjAngularUiBootstrapVersion
  val wjAngularUiCalendar = "org.webjars" % "angular-ui-calendar" % wjAngularUiCalendarVersion
  val wjAngularUiUtils = "org.webjars" % "angular-ui-utils" % wjAngularUiUtilsVersion
  val wjAngularStrap = "org.webjars" % "angular-strap" % wjAngularStrapVersion
  val wjBootstrap = "org.webjars" % "bootstrap" % wjBootstrapVersion
  val wjBootstrapDatepicker = "org.webjars" % "bootstrap-datepicker" % wjBsDatepickerVersion
  val wjBootstrapTimepicker = "org.webjars" % "bootstrap-timepicker" % wjBsTimepickerVersion
  val wjBootswatch = "org.webjars" % "bootswatch" % wjBootswatchVersion
  val wjFamfamFlag = "org.webjars" % "famfamfam-flags" % "0.0"
  val wjFontAwesome = "org.webjars" % "font-awesome" % wjFontAwesomeVersion
  val wjIonic = "org.webjars" % "ionic" % wjIonicVersion
  val wjJquery = "org.webjars" % "jquery" % wjJqueryVersion
  val wjJqueryUi = "org.webjars" % "jquery-ui" % wjJqueryUiVersion
  val wjMomentjs = "org.webjars" % "momentjs" % wjMomentjsVersion
  val wjPNtofify = "org.webjars" % "pnotify" % wjPNotifyVersion
  val wjSelect2 = "org.webjars" % "select2" % wjSelect2Version
}