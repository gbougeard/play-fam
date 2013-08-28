package controllers

import play.api.mvc._
import models.Substitution
import models.Substitutions._

import play.api.libs.json._


object Substitutions extends Controller with securesocial.core.SecureSocial {


  def jsonByMatchAndTeam(idMatch: Long, idTeam: Long) = Action {
    implicit request =>
      val subs = models.Substitutions.findByMatchAndTeam(idMatch, idTeam)
      Ok(Json.toJson(subs))

  }

}