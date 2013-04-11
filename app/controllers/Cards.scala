package controllers

import play.api.mvc._
import models.Card
import models.Cards._

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import play.api.libs.json._


object Cards extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[Card], "Minute")
  val timer = new Timer(metric)

  def jsonByMatchAndTeam(idMatch:Long, idTeam:Long) = Action {
    implicit request =>
      val cards = models.Cards.findByMatchAndTeam(idMatch, idTeam)
      Ok(Json.toJson(cards))
  }

}