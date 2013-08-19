package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models.CompetitionTeam


object CompetitionTeams extends Controller {

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
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (CompetitionTeam.apply)(CompetitionTeam.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  //  def index = Action {
  //    Home
  //  }

  //  def list = Action {
  //    val competitionTeams = models.CompetitionTeams.findAll
  //    val html = views.html.competitionTeams("Liste des competitionTeams", competitionTeams)
  //    Ok(html)
  //  }

  def byCompetition(id: Long) = Action {
    implicit request =>
      val competitionTeams = models.CompetitionTeams.findByCompetition(id)
      val competition = models.SeasonCompetitions.findByIdComplete(id)
      val html = views.html.competitionTeams.listTeam("Liste des competitionTeams", competition, competitionTeams)
      Ok(html)
  }

  def byTeam(id: Long) = Action {
    implicit request =>
      val competitionTeams = models.CompetitionTeams.findByTeam(id)
      val html = views.html.competitionTeams.list("Liste des competitionTeams", competitionTeams)
      Ok(html)
  }

  //  def view(id: Long) = Action {
  //    implicit request =>
  //      models.CompetitionTeams.findById(id).map {
  //        competitionTeam => Ok(views.html.competitionTeams.view("View CompetitionMatch", competitionTeam))
  //      } getOrElse (NotFound)
  //  }
  //
  //  def edit(id: Long) = Action {
  //    implicit request =>
  //      models.CompetitionTeams.findById(id).map {
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
  //          models.CompetitionTeams.update(id, competitionTeam)
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
  //          models.CompetitionTeams.insert(competitionTeam)
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
  //      models.CompetitionTeams.delete(id)
  //      Home.flashing("success" -> "CompetitionMatch has been deleted")
  //  }

}