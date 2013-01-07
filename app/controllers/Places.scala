package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Place
import models.common.AppDB
import models.common.JsonImplicits._
import slick.session.Session
import play.api.libs.json._
import play.api.libs.functional.syntax._


object Places extends Controller {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Places.list(0, 0))

  lazy val database = AppDB.database
  lazy val dal = AppDB.dal

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
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Place.apply)(Place.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val places = models.Places.findAll
  //    val html = views.html.places("Liste des places", places)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          val places = dal.Places.findPage(page, orderBy)
          val html = views.html.places.list("Liste des places", places, orderBy)
          Ok(html)
      }
  }

  def view(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.Places.findById(id).map {
            place => Ok(views.html.places.view("View Place", place))
          } getOrElse (NotFound)
      }
  }

  def edit(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.Places.findById(id).map {
            place => Ok(views.html.places.edit("Edit Place", id, placeForm.fill(place)))
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
          placeForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.places.edit("Edit Place - errors", id, formWithErrors)),
            place => {
              dal.Places.update(place)
              //        Home.flashing("success" -> "Place %s has been updated".format(place.name))
              Redirect(routes.Places.list(0, 2))
            }
          )
      }
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    Ok(views.html.places.create("New Place", placeForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          placeForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.places.create("New Place - errors", formWithErrors)),
            place => {
              dal.Places.insert(place)
              //        Home.flashing("success" -> "Place %s has been created".format(place.name))
              Redirect(routes.Places.list(0, 2))
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
        dal.Places.delete(id)
        Home.flashing("success" -> "Place has been deleted")
    }
  }


  /**
   * Display the 'new computer form'.
   */
  def gmap = Action {
        Ok(views.html.places.map("Map des places"))
  }

  def gmapData = Action {
    database.withSession {
      implicit session: Session =>
        val places = dal.Places.placesWithCoords
        Ok(Json.toJson(places))
    }
  }

}