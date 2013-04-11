package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Goal
import models.Goals._

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import play.api.libs.json._


object Goals extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[Goal], "Minute")
  val timer = new Timer(metric)

  def jsonByMatchAndTeam(idMatch:Long, idTeam:Long) = Action {
    implicit request =>
      val goals = models.Goals.findByMatchAndTeam(idMatch, idTeam)
      Ok(Json.toJson(goals))
  }

}