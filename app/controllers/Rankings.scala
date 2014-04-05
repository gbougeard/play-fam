package controllers

import play.api.mvc._
import models._



object Rankings extends Controller with securesocial.core.SecureSocial {


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
  //    val rankings = Ranking.findAll
  //    val html = views.html.rankings("Liste des rankings", rankings)
  //    Ok(html)
  //  }

  def list(competition: Long, orderBy: Int) = Action {
    implicit request =>
      val rankings = models.Rankings.findByCompetition(competition)
      val html = views.html.rankings.list("Liste des rankings", rankings, competition, orderBy, models.SeasonCompetitions.optionsChampionship)
      Ok(html)
  }


}