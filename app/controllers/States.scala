package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.State
import play.api.libs.json.Json
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import slick.session.Session


object States extends Controller {
  val metric = Metrics.defaultRegistry().newTimer(classOf[State], "page")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.States.list(0, 0))

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
      val states = models.States.findPage(page, orderBy)
      val html = views.html.states.list("Liste des states", states, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.States.findById(id).map {
        state => Ok(views.html.states.view("View State", state))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.States.findById(id).map {
        state => Ok(views.html.states.edit("Edit State", id, stateForm.fill(state), models.Countries.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      stateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.states.edit("Edit State - errors", id, formWithErrors, models.Countries.options)),
        state => {
          models.States.update(id, state)
          Redirect(routes.States.edit(id)).flashing("success" -> "State %s has been updated".format(state.name))
          //Redirect(routes.States.view(state.id))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.states.create("New State", stateForm, models.Countries.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      stateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.states.create("New State - errors", formWithErrors, models.Countries.options)),
        state => {
          models.States.insert(state)
          Redirect(routes.States.create).flashing("success" -> "State %s has been created".format(state.name))
          //Redirect(routes.States.view(state.id))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.States.delete(id)
      Home.flashing("success" -> "State has been deleted")
  }

}