import sbt._
import play.Project._
import sbt.Keys._
import com.lunatech.play.ubuntupackage.UbuntuPackagePlugin._

object Library {

  // Versions
  val akkaVersion = "2.2.0"
  val logbackVersion = "1.0.13"
  val slf4jVersion = "1.7.5"
  val mysqlVersion = "5.1.25"
  val jodaMapperVersion = "0.3.0"
  val playSlickVersion = "0.4.0"
  //  val metricsVersion                  = "3.0.0"
  val secureSocialVersion = "master-SNAPSHOT"
  val playMetricsVersion = "0.1.1"
  val webjarsVersion = "2.1.0-3"
  val wjAngularVersion = "1.1.5-1"
  val wjAngularUiVersion = "0.4.0-1"
  val wjAngularUiBootstrapVersion = "0.5.0"
  val wjAngularStrapVersion = "0.7.4"
  val wjBootstrapVersion = "3.0.0"
  val wjPNotifyVersion = "1.2.0"
  val wjRestangularVersion = "0.6.3"
  val wjLodashVersion = "1.3.1"

  // Libraries
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion
  val slf4j = "org.slf4j" % "slf4j-api" % slf4jVersion
  val mysql = "mysql" % "mysql-connector-java" % mysqlVersion
  val jodaMapper = "com.github.tototoshi" %% "slick-joda-mapper" % jodaMapperVersion
  val playSlick = "com.typesafe.play" %% "play-slick" % playSlickVersion
  //  val metrics               = "nl.grons" % "metrics-scala_2.10" % metricsVersion
  val playMetrics = "com.kenshoo" %% "metrics-play" % playMetricsVersion
  val secureSocial = "securesocial" %% "securesocial" % secureSocialVersion
  val webjars = "org.webjars" % "webjars-play_2.10" % webjarsVersion
  val wjAngular = "org.webjars" % "angularjs" % wjAngularVersion
  val wjAngularUi = "org.webjars" % "angular-ui" % wjAngularUiVersion
  val wjAngularUiBootstrap = "org.webjars" % "angular-ui-bootstrap" % wjAngularUiBootstrapVersion
  val wjAngularStrap = "org.webjars" % "angular-strap" % wjAngularStrapVersion
  val wjBootstrap = "org.webjars" % "bootstrap" % wjBootstrapVersion
  val wjPNtofify = "org.webjars" % "pnotify" % wjPNotifyVersion
  val wjRestangular = "org.webjars" % "restangular" % wjRestangularVersion
  val wjLodash = "org.webjars" % "lodash" % wjLodashVersion
}

object ApplicationBuild extends Build {

  import Library._

  val appName = "fam"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    slf4j,
    logbackClassic,
    mysql,
    jodaMapper,
        playSlick,
        playMetrics,
    secureSocial,
    webjars,
    wjAngular,
    wjAngularStrap,
    wjAngularUi,
    wjAngularUiBootstrap,
    wjBootstrap,
    wjPNtofify,
    wjLodash,
    wjRestangular

    //      ,anorm
    //    , "com.typesafe" % "slick_2.10" % "1.0.0-RC2"
    //    , "com.typesafe" % "play-slick_2.10" % "0.2.7-SNAPSHOT"
    //      ,"com.h2database" % "h2" % "1.3.166"
    //      ,"org.xerial" % "sqlite-jdbc" % "3.6.20"
    //      ,"org.slf4j" % "slf4j-nop" % "1.6.4" // <- disables logging

//,"com.typesafe.play" %% "play-slick" % "0.4.0"
// , "com.kenshoo" %% "metrics-play" % "0.1.1"
    // Other database drivers
    //    ,  "org.apache.derby" % "derby" % "10.6.1.0"
    //     , "org.hsqldb" % "hsqldb" % "2.0.0"
    //      ,"postgresql" % "postgresql" % "8.4-701.jdbc4"


    , "org.webjars" % "requirejs" % "2.1.8"
    , "org.webjars" % "bootstrap-datepicker" % "1.1.3"
    , "org.webjars" % "bootstrap-timepicker" % "0.2.3"
    , "org.webjars" % "bootswatch" % "2.3.1"
    , "org.webjars" % "font-awesome" % "3.2.1"
    , "org.webjars" % "jquery-ui" % "1.10.2-1"
    , "org.webjars" % "momentjs" % "2.1.0"
    , "org.webjars" % "tinymce-jquery" % "3.4.9"
    , "org.webjars" % "famfamfam-flags" % "0.0"
    , "org.webjars" % "x-editable-bootstrap" % "1.4.5"
    , "org.webjars" % "select2" % "3.4.1"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(ubuntuPackageSettings:_*).settings(
    requireJs += "mainProd.js",
    requireJsShim += "mainProd.js",

      UbuntuPackageKeys.maintainer := "Gregory Bougeard <gbougeard@gmail.com>",

      resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)

  // Add your own project settings here
    //  ).dependsOn(RootProject( uri("git://github.com/gbougeard/play-slick.git") ))
  )
//.dependsOn(RootProject(uri("git://github.com/freekh/play-slick.git")))
//    .dependsOn(RootProject(uri("git://github.com/kenshoo/metrics-play.git")))

}
