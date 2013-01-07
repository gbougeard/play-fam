package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{Event}
import models.common.AppDB

import slick.session.Session
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer


object Events extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[Event], "Minute")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Events.list(0, 0))

  lazy val database = AppDB.database
  lazy val dal = AppDB.dal

  /**
   * Describe the event form (used in both edit and create screens).
   */
  val eventForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "dtEvent" -> jodaDate,
      "duration" -> number,
      "name" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Event.apply)(Event.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val events = models.Events.findAll
  //    val html = views.html.events("Liste des events", events)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          val events =  timer.time(dal.Events.findPage(page, orderBy))
          val html = views.html.events.list("Liste des events", events, orderBy)
          Ok(html)
      }
  }

  def view(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.Events.findById(id).map {
            event => Ok(views.html.events.view("View Event", event))
          } getOrElse (NotFound)
      }
  }

  def edit(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.Events.findById(id).map {
            event => Ok(views.html.events.edit("Edit Event", id, eventForm.fill(event)))
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
          eventForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.events.edit("Edit Event - errors", id, formWithErrors)),
            event => {
              dal.Events.update(event)
              //        Home.flashing("success" -> "Event %s has been updated".format(event.name))
              Redirect(routes.Events.list(0, 2))
            }
          )
      }
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    Ok(views.html.events.create("New Event", eventForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          eventForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.events.create("New Event - errors", formWithErrors)),
            event => {
              dal.Events.insert(event)
              //        Home.flashing("success" -> "Event %s has been created".format(event.name))
              Redirect(routes.Events.list(0, 2))
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
        dal.Events.delete(id)
        Home.flashing("success" -> "Event has been deleted")
    }
  }

}