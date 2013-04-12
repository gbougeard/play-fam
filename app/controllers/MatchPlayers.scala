package controllers

import play.api.mvc._
import models.MatchPlayer
import models.MatchPlayers._

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import play.api.libs.json._
import play.api.Logger


object MatchPlayers extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[MatchPlayer], "Minute")
  val timer = new Timer(metric)

  def jsonByMatchAndTeam(idMatch:Long, idTeam:Long) = Action {
    implicit request =>
      Logger.debug("MatchPlayers "+idMatch +" "+idTeam)
      val mps = models.MatchPlayers.findByMatchAndTeam(idMatch, idTeam)
      Ok(Json.toJson(mps))
  }

}