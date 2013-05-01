package controllers

import play.Logger

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{EventTeam, FamUser, Event}
import models.Events._
import models.Teams._
import models.Places._
import models.TypEvents._
import models.EventTeams._

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import play.api.libs.json._

case class EventWithTeams(event:Event, listTeamId:Seq[Long])

object Events extends Controller with securesocial.core.SecureSocial {

  val metric = Metrics.defaultRegistry().newTimer(classOf[Event], "Page")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Events.list(0, 0))



  /**
   * Describe the event form (used in both edit and create screens).
   */
  val eventForm = Form(
    mapping(
      "event" -> mapping(
        "id" -> optional(longNumber),
        "dtEvent" -> jodaDate,
        "duration" -> number,
        "name" -> nonEmptyText,
        "typEventId" -> longNumber,
        "placeId" -> optional(longNumber),
        "eventStatusId" -> longNumber
      )(Event.apply)(Event.unapply),
       "teams" -> seq(longNumber)
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (EventWithTeams.apply)(EventWithTeams.unapply)
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
      val events = timer.time(models.Events.findPage(page, orderBy))
      val html = views.html.events.list("Liste des events", events, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Events.findById(id).map {
        event => Ok(views.html.events.view("View Event", event))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Events.findById(id).map {
        case (event, typEvent, eventStatus) =>
          val teams = models.EventTeams.findByEvent(id).map{_._3.id.get}
          Ok(views.html.events.edit("Edit Event", id, eventForm.fill(EventWithTeams(event, teams))))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = SecuredAction {
    implicit request =>
      eventForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.events.edit("Edit Event - errors", id, formWithErrors)),
        eventWithTeams => {
          models.Events.update(id, eventWithTeams.event)
          //        Home.flashing("success" -> "Event %s has been updated".format(event.name))
          Redirect(routes.Events.list(0, 2))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = SecuredAction {
    implicit request =>
      request.user match {
        case user: FamUser => // do whatever you need with your user class
          user.currentClubId.map {
            idClub =>
              Ok(views.html.events.create("New Event", Json.toJson(models.TypEvents.findAll).toString(), Json.toJson(models.Places.findAll).toString(),  Json.toJson(models.Teams.findByClub(idClub)).toString()))
          } getOrElse Unauthorized("You don't belong to any club")

        case _ => // did not get a User instance, should not happen,log error/thow exception
          Unauthorized("Not a valid user")
      }

  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action(parse.json) {
    implicit request =>
      val json = request.body
            println(json)
      val event = json.as[Event]
      val id = models.Events.insert(event)
      Ok(Json.toJson(id))
//      request.user match {
//        case user: FamUser => // do whatever you need with your user class
//          user.currentClubId.map {
//            idClub =>
//              eventForm.bindFromRequest.fold(
//                formWithErrors => BadRequest(views.html.events.create("New Event - errors", formWithErrors, Json.toJson(models.TypEvents.findAll).toString(), Json.toJson(models.Places.findAll).toString(), Json.toJson(models.Teams.findByClub(idClub)).toString())),
//                event => {
//                  models.Events.insert(event.event)
//                  //        Home.flashing("success" -> "Event %s has been created".format(event.name))
//                  Redirect(routes.Events.list(0, 2))
//                }
//              )
//          } getOrElse Unauthorized
//
//        case _ => // did not get a User instance, should not happen,log error/thow exception
//          Unauthorized("Not a valid user")
//      }
  }

  def saveTeams = Action(parse.json){
    implicit request =>
      val json = request.body
      println(json)
      val eventTeams = json.as[Seq[EventTeam]]
      models.EventTeams.deleteForEvent(eventTeams.head.eventId)
      eventTeams.map(eventTeam => models.EventTeams.insert(eventTeam))
      Ok
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.Events.delete(id)
      Home.flashing("success" -> "Event has been deleted")
  }

  /**
   * Display the 'new computer form'.
   */
  def agenda = Action {
    implicit request =>
      Ok(views.html.events.agenda("Agenda"))
  }

  def eventsData = Action {
    implicit request =>
      val events = models.Events.findAll
      Ok(Json.toJson(events))
  }

  def jsonById(id: Long) = Action {
    implicit request =>
      models.Events.findById(id).map {
        m => Ok(Json.toJson(m))
      } getOrElse (NotFound)

  }

}