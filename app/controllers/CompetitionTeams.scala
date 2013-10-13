package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._


object CompetitionTeams extends Controller with securesocial.core.SecureSocial {

  /**
   * This result directly redirect to the application home.
   */
  //  val Home = Redirect(routes.CompetitionTeams.list(0, 0))

  /**
   * Describe the competitionTeam form (used in both edit and create screens).
   */
  val competitionTeamForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "competitionId" -> longNumber,
      "teamId" -> longNumber
    )
      (CompetitionTeam.apply)(CompetitionTeam.unapply)
  )

  // -- Actions

  def byCompetition(id: Long) = Action {
    implicit request =>
      val competitionTeams = CompetitionTeam.findByCompetition(id)
      val competition = SeasonCompetition.findByIdComplete(id)
      val html = views.html.competitionTeams.listTeam("Liste des competitionTeams", competition, competitionTeams)
      Ok(html)
  }

  def byTeam(id: Long) = Action {
    implicit request =>
      val competitionTeams = CompetitionTeam.findByTeam(id)
      val html = views.html.competitionTeams.list("Liste des competitionTeams", competitionTeams)
      Ok(html)
  }

  //  def view(id: Long) = Action {
  //    implicit request =>
  //      CompetitionTeam.findById(id).map {
  //        competitionTeam => Ok(views.html.competitionTeams.view("View CompetitionMatch", competitionTeam))
  //      } getOrElse (NotFound)
  //  }
  //
  //  def edit(id: Long) = Action {
  //    implicit request =>
  //      CompetitionTeam.findById(id).map {
  //        competitionTeam => {
  //          play.Logger.debug("data "+competitionTeam)
  //          Ok(views.html.competitionTeams.edit("Edit CompetitionMatch", id, competitionTeamForm.fill(competitionTeam), models.Categorys.options, models.Scales.options, models.Competitions.options, models.TypCompetitions.options))
  //        }
  //      } getOrElse (NotFound)
  //  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  //  def update(id: Long) = Action {
  //    implicit request =>
  //      competitionTeamForm.bindFromRequest.fold(
  //        formWithErrors => BadRequest(views.html.competitionTeams.edit("Edit CompetitionMatch - errors", id, formWithErrors, models.Categorys.options, models.Scales.options, models.Competitions.options, models.TypCompetitions.options)),
  //        competitionTeam => {
  //          CompetitionTeam.update(id, competitionTeam)
  //          //        Home.flashing("success" -> "CompetitionMatch %s has been updated".format(competitionTeam.name))
  //          //Redirect(routes.CompetitionTeams.list(0, 2))
  //          Redirect(routes.CompetitionTeams.view(id)).flashing("success" -> "CompetitionMatch %s has been updated".format(competitionTeam.id))
  //
  //        }
  //      )
  //  }

  /**
   * Display the 'new computer form'.
   */
  //  def create = Action {
  //    implicit request =>
  //      Ok(views.html.competitionTeams.create("New CompetitionMatch", competitionTeamForm, models.Categorys.options, models.Scales.options, models.Competitions.options, models.TypCompetitions.options))
  //  }

  /**
   * Handle the 'new computer form' submission.
   */
  //  def save = Action {
  //    implicit request =>
  //      competitionTeamForm.bindFromRequest.fold(
  //        formWithErrors => BadRequest(views.html.competitionTeams.create("New CompetitionMatch - errors", formWithErrors, models.Categorys.options, models.Scales.options, models.Competitions.options, models.TypCompetitions.options)),
  //        competitionTeam => {
  //          CompetitionTeam.insert(competitionTeam)
  //          //        Home.flashing("success" -> "CompetitionMatch %s has been created".format(competitionTeam.name))
  //          Redirect(routes.CompetitionTeams.list(0, 2))
  //        }
  //      )
  //  }

  /**
   * Handle computer deletion.
   */
  //  def delete(id: Long) = Action {
  //    implicit request =>
  //      CompetitionTeam.delete(id)
  //      Home.flashing("success" -> "CompetitionMatch has been deleted")
  //  }

}