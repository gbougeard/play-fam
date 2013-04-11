package controllers

import play.api.mvc._
import models.Substitution

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import play.api.libs.json._


object Substitutions extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[Substitution], "Minute")
  val timer = new Timer(metric)

  def jsonByMatchAndTeam(idMatch:Long, idTeam:Long) = Action {
    implicit request =>
      val goals = models.Substitutions.findByMatchAndTeam(idMatch, idTeam)
      Ok(Json.toJson(goals))
  }

}