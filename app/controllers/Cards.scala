package controllers

import play.api.mvc._
import models._

import play.api.libs.json._

object Cards extends Controller {


  def jsonByMatchAndTeam(idMatch: Long, idTeam: Long) = Action {
    implicit request =>
      val cards = Card.findByMatchAndTeam(idMatch, idTeam)
      Ok(Json.toJson(cards))
  }

}