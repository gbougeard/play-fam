package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Place
import models.Places._
import slick.session.Session
import play.api.libs.json._
import play.api.libs.functional.syntax._
import service.{Coach,Administrator}


object Places extends Controller  with securesocial.core.SecureSocial{

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Places.list(0, 0))

  /**
   * Describe the place form (used in both edit and create screens).
   */
  val placeForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "city" -> nonEmptyText,
      "zipcode" -> number,
      "latitude" -> optional(ignored(0.0f)),
      "longitude" -> optional(ignored(0.0f))
    )
      (Place.apply)(Place.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val places = models.Places.findPage(page, orderBy)
      val html = views.html.places.list("Liste des places", places, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Places.findById(id).map {
        place => Ok(views.html.places.view("View Place", place))
      } getOrElse (NotFound)
  }

  def edit(id: Long) =  SecuredAction(WithRoles(List(Coach)))  {
    implicit request =>
      models.Places.findById(id).map {
        place => Ok(views.html.places.edit("Edit Place", id, placeForm.fill(place)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(List(Coach)))  {
    implicit request =>
      placeForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.places.edit("Edit Place - errors", id, formWithErrors)),
        place => {
          models.Places.update(id, place)
          //        Home.flashing("success" -> "Place %s has been updated".format(place.name))
          Redirect(routes.Places.list(0, 2))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(List(Coach)))  {
    implicit request =>
      Ok(views.html.places.create("New Place", placeForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(List(Coach)))  {
    implicit request =>
      placeForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.places.create("New Place - errors", formWithErrors)),
        place => {
          models.Places.insert(place)
          //        Home.flashing("success" -> "Place %s has been created".format(place.name))
          Redirect(routes.Places.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(List(Administrator)))  {
    implicit request =>
      models.Places.delete(id)
      Home.flashing("success" -> "Place has been deleted")
  }


  /**
   * Display the 'new computer form'.
   */
  def gmap = Action {
    implicit request =>
      Ok(views.html.places.map("Map des places"))
  }

  def gmapData = Action {
    implicit request =>
      val places = models.Places.placesWithCoords
      Ok(Json.toJson(places))
  }

  def jsonList = Action {
    implicit request =>
      Ok(Json.toJson(models.Places.findAll))
  }

}