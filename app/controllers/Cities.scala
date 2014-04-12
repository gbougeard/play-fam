package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

import json.ImplicitGlobals._
import play.api.libs.json.Json
import service.Administrator


object Cities extends Controller  with securesocial.core.SecureSocial {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Cities.list(0, 0))

  /**
   * Describe the city form (used in both edit and create screens).
   */
  val cityForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "upper" -> nonEmptyText,
      "lower" -> nonEmptyText,
      "provinceId" -> longNumber
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (City.apply)(City.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Cities.findById(id).map {
        city =>
          render {
            case Accepts.Html() => Ok(views.html.cities.view("View City", city))
            case Accepts.Json() => Ok(Json.toJson(city))
          }
      } getOrElse NotFound
  }

  def edit(id: Long) = SecuredAction(WithRoles(Set(Administrator))) {
    implicit request =>
      models.Cities.findById(id).map {
        city => Ok(views.html.cities.edit("Edit City", id, cityForm.fill(city), models.Provinces.options))
      } getOrElse NotFound
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = SecuredAction(WithRoles(Set(Administrator))) {
    implicit request =>
      cityForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.cities.edit("Edit City - errors", id, formWithErrors, models.Provinces.options)),
        city => {
          models.Cities.update(id, city)
          Redirect(routes.Cities.edit(id)).flashing("success" -> "City %s has been updated".format(city.name))
          //Redirect(routes.Cities.view(city.id.get))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = SecuredAction(WithRoles(Set(Administrator))) {
    implicit request =>
      Ok(views.html.cities.create("New City", cityForm, models.Provinces.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = SecuredAction(WithRoles(Set(Administrator))) {
    implicit request =>
      cityForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.cities.create("New City - errors", formWithErrors, models.Provinces.options)),
        city => {
          models.Cities.insert(city)
          //        Home.flashing("success" -> "City %s has been created".format(city.name))
          //Redirect(routes.Cities.view(city.id))
          Redirect(routes.Cities.create).flashing("success" -> "City %s has been created".format(city.name))

        }
      )
  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val cities = models.Cities.findPage(page, orderBy)
      render {
        case Accepts.Html() => Ok( views.html.cities.list("Liste des cities", cities, orderBy))
        case Accepts.Json() => Ok(Json.toJson(cities))
      }
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = SecuredAction(WithRoles(Set(Administrator))) {
    implicit request =>
      models.Cities.delete(id)
      Home.flashing("success" -> "City has been deleted")
  }

}