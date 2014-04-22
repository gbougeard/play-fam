package controllers

import play.api.mvc._
import models.CardJson._

import play.api.libs.json._


object Cards extends Controller {


  def byMatchAndTeam(idMatch: Long, idTeam: Long) = Action {
    implicit request =>
      render {
        case Accepts.Json() =>
          val cards = models.Cards.findByMatchAndTeam(idMatch, idTeam)
          Ok(Json.toJson(cards))
      }
  }

}