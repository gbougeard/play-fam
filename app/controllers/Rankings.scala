package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Ranking
import metrics.Instrumented


object Rankings extends Controller with Instrumented{

  private[this] val timer = metrics.timer("RankingLoad")


  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    implicit request =>
      val competitions = models.SeasonCompetitions.findAll
      val html = views.html.rankings.index("Rankings", competitions)
      Ok(html)
  }

  //  def list = Action {
  //    val rankings = models.Rankings.findAll
  //    val html = views.html.rankings("Liste des rankings", rankings)
  //    Ok(html)
  //  }

  def list(competition: Long, orderBy: Int) = Action {
    implicit request =>
      val rankings = timer.time(models.Rankings.findByCompetition(competition))
      val html = views.html.rankings.list("Liste des rankings", rankings, competition, orderBy, models.SeasonCompetitions.optionsChampionship)
      Ok(html)
  }


}