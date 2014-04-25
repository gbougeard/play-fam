package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import models.PageJson._
import models.TeamJson._
import models.TeamJsonExt._

import service.{Administrator, Coach}
import play.api.libs.json.Json


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
      val teams = models.Teams.findPage(page, orderBy)
      render {
        case Accepts.Html() => Ok(views.html.teams.list("Liste des teams", teams, orderBy))
        case Accepts.Json() => Ok(Json.toJson(teams))
      }
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Teams.findById(id).map {
        team =>
          render {
            case Accepts.Html() => Ok(views.html.teams.view("View Team", team))
            case Accepts.Json() => Ok(Json.toJson(team))
          }
      } getOrElse NotFound
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      models.Teams.findById(id).map {
        team => Ok(views.html.teams.edit("Edit Team", id, teamForm.fill(team), models.Clubs.options))
      } getOrElse NotFound
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      teamForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.teams.edit("Edit Team - errors", id, formWithErrors, models.Clubs.options)),
        team => {
          models.Teams.update(id, team)
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
      Ok(views.html.teams.create("New Team", teamForm, models.Clubs.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      teamForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.teams.create("New Team - errors", formWithErrors, models.Clubs.options)),
        team => {
          models.Teams.insert(team)
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
      models.Teams.delete(id)
      Home.flashing("success" -> "Team has been deleted")
  }

}