package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Team
import models.common.{AppDB, DBeable}
import play.api.Play.current
import slick.session.Session


object Teams extends Controller {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Teams.list(0, 0))

  lazy val database = AppDB.database
  lazy val dal = AppDB.dal
  /**
   * Describe the team form (used in both edit and create screens).
   */
  val teamForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "clubId" -> longNumber
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Team.apply)(Team.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val teams = models.Teams.findAll
  //    val html = views.html.teams("Liste des teams", teams)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          val teams = dal.Teams.findPage(page, orderBy)
          val html = views.html.teams.list("Liste des teams", teams, orderBy)
          Ok(html)
      }
  }

  def view(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.Teams.findById(id).map {
            team => Ok(views.html.teams.view("View Team", team))
          } getOrElse (NotFound)
      }
  }

  def edit(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.Teams.findById(id).map {
            team => Ok(views.html.teams.edit("Edit Team", id, teamForm.fill(team), dal.Clubs.options))
          } getOrElse (NotFound)
      }
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          teamForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.teams.edit("Edit Team - errors", id, formWithErrors, dal.Clubs.options)),
            team => {
              dal.Teams.update(team)
              //        Home.flashing("success" -> "Team %s has been updated".format(team.name))
              Redirect(routes.Teams.list(0, 2))
            }
          )
      }
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    database.withSession {
      implicit session: Session =>
        Ok(views.html.teams.create("New Team", teamForm, dal.Clubs.options))
    }
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          teamForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.teams.create("New Team - errors", formWithErrors, dal.Clubs.options)),
            team => {
              dal.Teams.insert(team)
              //        Home.flashing("success" -> "Team %s has been created".format(team.name))
              Redirect(routes.Teams.list(0, 2))
            }
          )
      }
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    database.withSession {
      implicit session: Session =>
        dal.Teams.delete(id)
        Home.flashing("success" -> "Team has been deleted")
    }
  }

}