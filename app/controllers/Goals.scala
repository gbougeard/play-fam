package controllers

import play.api.mvc._
import models.Goal
import models.Goals._

import play.api.libs.json._


object Goals extends Controller with securesocial.core.SecureSocial {


  def jsonByMatchAndTeam(idMatch: Long, idTeam: Long) = Action {
    implicit request =>
      val goals = models.Goals.findByMatchAndTeam(idMatch, idTeam)
      Ok(Json.toJson(goals))
  }

}