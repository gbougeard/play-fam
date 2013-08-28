package controllers

import play.api.mvc._
import models.MatchTeams._

import play.api.libs.json._


object MatchTeams extends Controller with securesocial.core.SecureSocial {


  def jsonByMatchAndTeam(idMatch: Long, idTeam: Long) = Action {
    implicit request =>
      Ok(Json.toJson(models.MatchTeams.findByMatchAndTeam(idMatch, idTeam)))
  }

  def jsonByMatchAndHome(idMatch: Long) = Action {
    implicit request =>
      val goals = models.MatchTeams.findByMatchAndHome(idMatch)
      Ok(Json.toJson(goals))
  }

  def jsonByMatchAndAway(idMatch: Long) = Action {
    implicit request =>
      models.MatchTeams.findByMatchAndAway(idMatch).map {
        goals => Ok(Json.toJson(goals))
      } getOrElse NotFound
  }

}