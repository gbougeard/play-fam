package controllers

import play.api.mvc._
import models._
import models.MatchPlayerJson._

import play.api.libs.json._
import play.api.Logger



object MatchPlayers extends Controller with securesocial.core.SecureSocial {


  def jsonByMatchAndTeam(idMatch: Long, idTeam: Long) = Action {
    implicit request =>
      Logger.debug("MatchPlayers " + idMatch + " " + idTeam)
      val mps = models.MatchPlayers.findByMatchAndTeam(idMatch, idTeam)
      Ok(Json.toJson(mps))
  }

}