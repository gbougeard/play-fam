package controllers

import play.Logger

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import service.{Administrator, Coach}

import play.api.libs.json._
import scala.util.{Failure, Success}

case class EventWithTeams(event: Event, teams: Seq[Long])

object Events extends Controller with securesocial.core.SecureSocial {
  implicit val eventWithTeamsFormat = Json.format[EventWithTeams]


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
        "eventStatusId" -> longNumber,
        "comments" -> optional(text)
      )(Event.apply)(Event.unapply),
      "teams" -> seq(longNumber)
    )
      (EventWithTeams.apply)(EventWithTeams.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val events = Event.findPage(page, orderBy)
      render {
        case Accepts.Html() => Ok( views.html.events.list("Liste des events", events, orderBy))
        case Accepts.Json() => Ok(Json.toJson(events))
      }
  }

  def view(id: Long) = Action {
    implicit request =>
      Event.findById(id).map {
        event =>
          render {
            case Accepts.Html() => Ok(views.html.events.view("View Event", event, EventTeam.findByEvent(id)))
            case Accepts.Json() => Ok(Json.toJson(event))
          }
      } getOrElse NotFound
  }

  def edit(id: Long) = Action { //SecuredAction(WithRoles(Set(Coach))) {
    implicit request =>
//      request.user match {
//        case user: FamUser => // do whatever you need with your user class
//          user.currentClubId.map {
//            idClub =>
              val teams = EventTeam.findByEvent(id).map {
                x => x._3.id.get
              }
              Event.findById(id).map {
                case (event, typEvent, eventStatus) =>
                  Ok(views.html.events.edit("Edit Event", 1, Json.toJson(EventWithTeams(event, teams)).toString(), Json.toJson(Team.findByClub(1)).toString()))
                case _ =>
                  NotFound

              } getOrElse NotFound
//
//          } getOrElse Unauthorized //("You don't belong to any club")
//
//        case _ => // did not get a User instance, should not happen,log error/thow exception
//          Unauthorized("Not a valid user")
//      }

  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  //  def update(id: Long) = SecuredAction {
  //    implicit request =>
  //      eventForm.bindFromRequest.fold(
  //        formWithErrors => BadRequest(views.html.events.edit("Edit Event - errors", id, formWithErrors)),
  //        eventWithTeams => {
  //          Event.update(id, eventWithTeams.event)
  //          //        Home.flashing("success" -> "Event %s has been updated".format(event.name))
  //          Redirect(routes.Events.list(0, 2))
  //        }
  //      )
  //  }
  def update(id: Long) = Action(parse.json) {
    implicit request =>
      val json = request.body
      val event = json.as[Event]
      Event.update(id, event)
      Ok(Json.toJson(id))
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action { //SecuredAction(WithRoles(Set(Coach))) {
    implicit request =>
//      request.user match {
//        case user: FamUser => // do whatever you need with your user class
//          user.currentClubId.map {
//            idClub =>
              Ok(views.html.events.create("New Event", 1, Json.toJson(Team.findByClub(1)).toString()))
//          } getOrElse Unauthorized("You don't belong to any club")
//
//        case _ => // did not get a User instance, should not happen,log error/thow exception
//          Unauthorized("Not a valid user")
//      }

  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action(parse.json) {
    implicit request =>
      val json = request.body
      val event = json.as[Event]
      val id = Event.insert(event)
      Ok(Json.toJson(id))
  }

  def saveTeams = Action(parse.json) {
    implicit request =>
      val json = request.body
      val eventTeams = json.as[Seq[EventTeam]]

      val id = eventTeams.head.eventId
      EventTeam.deleteForEvent(id)
      EventTeam.insert(eventTeams) match {
        case Success(a) => {
          Ok
        }
        case Failure(e) => {
          Logger.error("update error:", e)
          BadRequest(Json.toJson(Map("error" -> e.getMessage)))
        }
      }
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = SecuredAction(WithRoles(Set(Administrator))) {
    implicit request =>
      Event.delete(id)
      Home.flashing("success" -> "Event has been deleted")
  }

  /**
   * Display the 'new computer form'.
   */
  def agenda = Action {
    implicit request =>
      render {
        case Accepts.Html() => Ok(views.html.events.agenda("Agenda"))
        case Accepts.Json() => Ok(Json.toJson(Event.findAll))
      }
  }

}