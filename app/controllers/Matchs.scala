package controllers

import _root_.securesocial.core.Authorization
import _root_.securesocial.core.Identity

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Match

import play.api.Logger

object Matchs extends Controller with securesocial.core.SecureSocial {


  /**
   * Describe the match form (used in both edit and create screens).
   */
  val matchForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "fixtureId" -> optional(longNumber),
      "competitionId" -> longNumber,
      "eventId" -> optional(longNumber)
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Match.apply)(Match.unapply)
  )

  // -- Actions

  //  def list = Action {
  //    val matchs = models.Matchs.findAll
  //    val html = views.html.matchs("Liste des matchs", matchs)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = SecuredAction {
    implicit request =>
      val matchs = models.Matchs.findPage(page, orderBy)
      val html = views.html.matchs.list("Liste des matchs", matchs, orderBy)
      Ok(html)
  }

  def view(id: Long) = SecuredAction {
    implicit request =>
      models.Matchs.findById(id).map {
        m => {
          models.Events.findById(m.eventId.getOrElse(0)).map {
            event => {
              models.MatchTeams.findByMatchAndHome(id).map {
                case (home, homeTeam) => {
                  val homeGoals = models.Goals.findByMatchAndTeam(id,  homeTeam.id.getOrElse(0))
                  val homePlayers = models.MatchPlayers.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
                  val homeSubs = models.Substitutions.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
                  val homeCards = models.Cards.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))

                  models.MatchTeams.findByMatchAndAway(id).map {
                  case  (away, awayTeam ) => {
                    val awayGoals = models.Goals.findByMatchAndTeam(id,  awayTeam.id.getOrElse(0))
                    val awayPlayers = models.MatchPlayers.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
                    val awaySubs = models.Substitutions.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
                    val awayCards = models.Cards.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
                      Ok(views.html.matchs.view("View Match", m, event, (home,homeTeam), (away, awayTeam), homeGoals, awayGoals,  homePlayers, awayPlayers, homeSubs, awaySubs, homeCards, awayCards))
                    }
                  } getOrElse (NotFound)
                }
              } getOrElse (NotFound)
            }
          } getOrElse (NotFound)
        }
      } getOrElse (NotFound)
  }

  def edit(id: Long) = SecuredAction {
    implicit request =>
      models.Matchs.findById(id).map {
        m => Ok(views.html.matchs.edit("Edit Match", id, matchForm.fill(m)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      Logger.info("update match " + id)
      matchForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.matchs.edit("Edit Match - errors", id, formWithErrors)),
        m => {
          models.Matchs.update(id, m)
          Redirect(routes.Matchs.edit(id)).flashing("success" -> "Match %s has been updated".format(m.id))
          //          Redirect(routes.Matchs.list(0, 2))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = SecuredAction {
    implicit request =>
      Ok(views.html.matchs.create("New Match", matchForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = SecuredAction {
    implicit request =>
      matchForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.matchs.create("New Match - errors", formWithErrors)),
        m => {
          models.Matchs.insert(m)
          //        Home.flashing("success" -> "Match %s has been created".format(match.name))
          Redirect(routes.Matchs.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = SecuredAction {
    implicit request =>
      models.Matchs.delete(id)
      Redirect(routes.Matchs.list(0, 0)).flashing("success" -> "Match has been deleted")
  }

}