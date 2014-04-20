package controllers

import play.api.mvc._
import models._
import models.CardJson._

import play.api.libs.json._


object Cards extends Controller {


  def jsonByMatchAndTeam(idMatch: Long, idTeam: Long) = Action {
    implicit request =>
      val cards = models.Cards.findByMatchAndTeam(idMatch, idTeam)
      Ok(Json.toJson(cards))
  }

}