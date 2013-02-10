import sbt._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "fam"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc
    //      ,anorm
//    , "com.typesafe" % "slick_2.10" % "1.0.0-RC2"
    , "com.typesafe" % "play-slick_2.10" % "0.2.7-SNAPSHOT"
    //      ,"com.h2database" % "h2" % "1.3.166"
    //      ,"org.xerial" % "sqlite-jdbc" % "3.6.20"
    //      ,"org.slf4j" % "slf4j-nop" % "1.6.4" // <- disables logging

    // enables logging
    , "org.slf4j" % "slf4j-api" % "1.6.4"
    , "ch.qos.logback" % "logback-classic" % "0.9.28"


    // Other database drivers
    //    ,  "org.apache.derby" % "derby" % "10.6.1.0"
    //     , "org.hsqldb" % "hsqldb" % "2.0.0"
    //      ,"postgresql" % "postgresql" % "8.4-701.jdbc4"

    , "mysql" % "mysql-connector-java" % "5.1.13"
    , "com.yammer.metrics" % "metrics-scala_2.9.1" % "2.1.5"
    , "com.yammer.metrics" % "metrics-graphite" % "2.1.5"

    , "org.webjars" % "webjars-play" % "2.1-RC1"
    //    , "org.webjars" % "requirejs" % "2.1.1"
    , "org.webjars" % "bootstrap" % "2.3.0"
    //    , "org.webjars" % "bootstrap-datepicker" % "2.2.1"
    , "org.webjars" % "momentjs" % "1.7.2"
    , "org.webjars" % "angularjs" % "1.1.2"
    , "org.webjars" % "angular-ui" % "0.3.2-1"
    , "org.webjars" % "angular-strap" % "0.6.3"
    , "org.webjars" % "font-awesome" % "3.0.0"
    , "org.webjars" % "jquery" % "1.9.0"
    , "org.webjars" % "jquery-ui" % "1.9.2"
    , "org.webjars" % "tinymce-jquery" % "3.4.9"
    , "org.webjars" % "famfamfam-flags" % "0.0"

  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
//      ).dependsOn(RootProject( uri("git://github.com/gbougeard/play-slick.git") ))
  ).dependsOn(RootProject( uri("git://github.com/freekh/play-slick.git") ))



}
