package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.EventStatus
import models.EventStatuses._

import play.api.libs.json._

import metrics.Instrumented


object EventStatuses extends Controller  with Instrumented {
  private[this] val timer = metrics.timer("count")



  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.EventStatuses.list(0, 0))

  /**
   * Describe the eventStatus form (used in both edit and create screens).
   */
  val eventStatusForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (EventStatus.apply)(EventStatus.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val eventStatuses = models.EventStatuses.findAll
  //    val html = views.html.eventStatuses("Liste des eventStatuses", eventStatuses)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val eventStatuses = timer.time(models.EventStatuses.findPage(page, orderBy))
      val html = views.html.eventStatuses.list("Liste des eventStatuses", eventStatuses, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.EventStatuses.findById(id).map {
        eventStatus => Ok(views.html.eventStatuses.view("View EventStatus", eventStatus))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.EventStatuses.findById(id).map {
        eventStatus => Ok(views.html.eventStatuses.edit("Edit EventStatus", id, eventStatusForm.fill(eventStatus)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      eventStatusForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.eventStatuses.edit("Edit EventStatus - errors", id, formWithErrors)),
        eventStatus => {
          models.EventStatuses.update(id, eventStatus)
          //        Home.flashing("success" -> "EventStatus %s has been updated".format(eventStatus.name))
          //Redirect(routes.EventStatuses.list(0, 2))
          Redirect(routes.EventStatuses.view(id)).flashing("success" -> "EventStatus %s has been updated".format(eventStatus.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.eventStatuses.create("New EventStatus", eventStatusForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      eventStatusForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.eventStatuses.create("New EventStatus - errors", formWithErrors)),
        eventStatus => {
          models.EventStatuses.insert(eventStatus)
          //        Home.flashing("success" -> "EventStatus %s has been created".format(eventStatus.name))
          Redirect(routes.EventStatuses.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.EventStatuses.delete(id)
      Home.flashing("success" -> "EventStatus has been deleted")
  }

  def jsonList = Action {
    implicit request =>
      Ok(Json.toJson(models.EventStatuses.findAll))
  }

}