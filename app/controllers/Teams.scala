package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import service.{Administrator, Coach}


object Teams extends Controller with securesocial.core.SecureSocial{

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Teams.list(0, 0))

  /**
   * Describe the team form (used in both edit and create screens).
   */
  val teamForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "clubId" -> longNumber
    )
      (Team.apply)(Team.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val teams = Team.findPage(page, orderBy)
      val html = views.html.teams.list("Liste des teams", teams, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      Team.findById(id).map {
        team => Ok(views.html.teams.view("View Team", team))
      } getOrElse (NotFound)
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      Team.findById(id).map {
        team => Ok(views.html.teams.edit("Edit Team", id, teamForm.fill(team), Club.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      teamForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.teams.edit("Edit Team - errors", id, formWithErrors, Club.options)),
        team => {
          Team.update(id, team)
          //        Home.flashing("success" -> "Team %s has been updated".format(team.name))
          Redirect(routes.Teams.list(0, 2))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      Ok(views.html.teams.create("New Team", teamForm, Club.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      teamForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.teams.create("New Team - errors", formWithErrors, Club.options)),
        team => {
          Team.insert(team)
          //        Home.flashing("success" -> "Team %s has been created".format(team.name))
          Redirect(routes.Teams.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      Team.delete(id)
      Home.flashing("success" -> "Team has been deleted")
  }

}