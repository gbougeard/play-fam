package controllers

import play.api.mvc._
import models.MatchTeam
import models.MatchTeams._

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import play.api.libs.json._


object MatchTeams extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[MatchTeam], "Minute")
  val timer = new Timer(metric)

  def jsonByMatchAndHome(idMatch: Long) = Action {
    implicit request =>
      val goals = models.MatchTeams.findByMatchAndHome(idMatch)
      Ok(Json.toJson(goals))
  }

  def jsonByMatchAndAway(idMatch: Long) = Action {
    implicit request =>
      models.MatchTeams.findByMatchAndAway(idMatch).map {
        goals => Ok(Json.toJson(goals))
      } getOrElse (NotFound)
  }

}