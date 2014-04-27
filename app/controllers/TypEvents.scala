package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import models.PageJson._
import models.TypEventJson._

import play.api.libs.json._
import service.Administrator



object TypEvents extends Controller with securesocial.core.SecureSocial {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.TypEvents.list(0, 0))

  /**
   * Describe the typEvent form (used in both edit and create screens).
   */
  val typEventForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText
    )
      (TypEvent.apply)(TypEvent.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val typEvents = models.TypEvents.findPage(page, orderBy)
      render {
        case Accepts.Html() => Ok(views.html.typEvents.list("Liste des typEvents", typEvents, orderBy))
        case Accepts.Json() => Ok(Json.toJson(typEvents))
      }
  }

  def view(id: Long) = Action {
    implicit request =>
      models.TypEvents.findById(id).map {
        typEvent =>  render {
          case Accepts.Html() => Ok(views.html.typEvents.view("View TypEvent", typEvent))
          case Accepts.Json() => Ok(Json.toJson(typEvent))
        }
      } getOrElse NotFound
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      models.TypEvents.findById(id).map {
        typEvent => Ok(views.html.typEvents.edit("Edit TypEvent", id, typEventForm.fill(typEvent)))
      } getOrElse NotFound
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      typEventForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typEvents.edit("Edit TypEvent - errors", id, formWithErrors)),
        typEvent => {
          models.TypEvents.update(id, typEvent)
          //        Home.flashing("success" -> "TypEvent %s has been updated".format(typEvent.name))
          //Redirect(routes.TypEvents.list(0, 2))
          Redirect(routes.TypEvents.view(id)).flashing("success" -> "TypEvent %s has been updated".format(typEvent.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      Ok(views.html.typEvents.create("New TypEvent", typEventForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      typEventForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typEvents.create("New TypEvent - errors", formWithErrors)),
        typEvent => {
          models.TypEvents.insert(typEvent)
          //        Home.flashing("success" -> "TypEvent %s has been created".format(typEvent.name))
          Redirect(routes.TypEvents.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      models.TypEvents.delete(id)
      Home.flashing("success" -> "TypEvent has been deleted")
  }

  def jsonList = Action {
    implicit request =>
      Ok(Json.toJson(models.TypEvents.findAll))
  }

}