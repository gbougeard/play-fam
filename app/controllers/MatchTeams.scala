package controllers

import play.api.mvc._
import models.MatchTeams._

import play.api.libs.json._

import metrics.Instrumented


object MatchTeams extends Controller  with Instrumented {
  private[this] val timer = metrics.timer("count")



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