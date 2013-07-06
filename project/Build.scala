import sbt._
import play.Project._
import sbt.Keys._

object ApplicationBuild extends Build {

  val appName = "fam"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc
    //      ,anorm
    //    , "com.typesafe" % "slick_2.10" % "1.0.0-RC2"
    //    , "com.typesafe" % "play-slick_2.10" % "0.2.7-SNAPSHOT"
    //      ,"com.h2database" % "h2" % "1.3.166"
    //      ,"org.xerial" % "sqlite-jdbc" % "3.6.20"
    //      ,"org.slf4j" % "slf4j-nop" % "1.6.4" // <- disables logging

    // enables logging
    , "org.slf4j" % "slf4j-api" % "1.7.5"
    , "ch.qos.logback" % "logback-classic" % "1.0.13"

    // Other database drivers
    //    ,  "org.apache.derby" % "derby" % "10.6.1.0"
    //     , "org.hsqldb" % "hsqldb" % "2.0.0"
    //      ,"postgresql" % "postgresql" % "8.4-701.jdbc4"
    , "mysql" % "mysql-connector-java" % "5.1.25"
    , "com.github.tototoshi" %% "slick-joda-mapper" % "0.3.0"
    // Metrics
    , "nl.grons" % "metrics-scala_2.10" % "3.0.0"
    //    , "com.yammer.metrics" % "metrics-scala_2.10" % "2.2.0"
    //    , "com.yammer.metrics" % "metrics-graphite" % "2.2.0"

    , "securesocial" %% "securesocial" % "master-SNAPSHOT"

    , "org.webjars" % "webjars-play_2.10" % "2.1.0-2"
    //    , "org.webjars" % "requirejs" % "2.1.1"
    , "org.webjars" % "angularjs" % "1.1.5-1"
    , "org.webjars" % "angular-ui" % "0.4.0-1"
    , "org.webjars" % "angular-ui-bootstrap" % "0.4.0"
    , "org.webjars" % "angular-strap" % "0.7.4"
    , "org.webjars" % "bootstrap" % "2.3.2"
    , "org.webjars" % "bootstrap-datepicker" % "1.0.1"
    , "org.webjars" % "bootstrap-timepicker" % "0.2.3"
    , "org.webjars" % "bootswatch" % "2.3.1"
    , "org.webjars" % "font-awesome" % "3.2.1"
    , "org.webjars" % "jquery-ui" % "1.10.2-1"
    , "org.webjars" % "momentjs" % "2.0.0-1"
    , "org.webjars" % "tinymce-jquery" % "3.4.9"
    , "org.webjars" % "famfamfam-flags" % "0.0"
    , "org.webjars" % "x-editable-bootstrap" % "1.4.5"
    , "org.webjars" % "select2" % "3.4.1"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
    // Add your own project settings here
    //  ).dependsOn(RootProject( uri("git://github.com/gbougeard/play-slick.git") ))
  ).dependsOn(RootProject(uri("git://github.com/freekh/play-slick.git")))

}
