package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.State
import play.api.libs.json.Json
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import models.common.AppDB
import slick.session.Session


object States extends Controller {
  val metric = Metrics.defaultRegistry().newTimer(classOf[State], "page")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.States.list(0, 0))

  lazy val database = AppDB.database
  lazy val dal = AppDB.dal
  /**
   * Describe the state form (used in both edit and create screens).
   */
  val stateForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "upper" -> nonEmptyText,
      "lower" -> nonEmptyText,
      "countryId" -> longNumber
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (State.apply)(State.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          val states = dal.States.findPage(page, orderBy)
          val html = views.html.states.list("Liste des states", states, orderBy)
          Ok(html)
      }
  }

  def view(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.States.findById(id).map {
            state => Ok(views.html.states.view("View State", state))
          } getOrElse (NotFound)
      }
  }

  def edit(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.States.findById(id).map {
            state => Ok(views.html.states.edit("Edit State", id, stateForm.fill(state), dal.Countries.options))
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
          stateForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.states.edit("Edit State - errors", id, formWithErrors, dal.Countries.options)),
            state => {
              dal.States.update(state)
              Redirect(routes.States.edit(id)).flashing("success" -> "State %s has been updated".format(state.name))
              //Redirect(routes.States.view(state.id))
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
        Ok(views.html.states.create("New State", stateForm, dal.Countries.options))
    }
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          stateForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.states.create("New State - errors", formWithErrors, dal.Countries.options)),
            state => {
              dal.States.insert(state)
              Redirect(routes.States.create).flashing("success" -> "State %s has been created".format(state.name))
              //Redirect(routes.States.view(state.id))
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
        dal.States.delete(id)
        Home.flashing("success" -> "State has been deleted")
    }
  }

}