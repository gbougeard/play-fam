package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Country
import play.api.libs.json.Json
import slick.session.Session
import service.Administrator


object Countries extends Controller with securesocial.core.SecureSocial {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Countries.list(0, 0))

  /**
   * Describe the country form (used in both edit and create screens).
   */
  val countryForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "upper" -> nonEmptyText,
      "lower" -> nonEmptyText
    )
      (Country.apply)(Country.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val countries = models.Countries.findPage(page, orderBy)
     Ok(views.html.countries.list("Liste des countries", countries, orderBy))
  }


  def view(id: Long) = Action {
    implicit request =>
      models.Countries.findById(id).map {
        country => Ok(views.html.countries.view("View Country", country))
      } getOrElse (NotFound)
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      models.Countries.findById(id).map {
        country => Ok(views.html.countries.edit("Edit Country", id, countryForm.fill(country)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      countryForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.countries.edit("Edit Country - errors", id, formWithErrors)),
        country => {
          models.Countries.update(id, country)
          Redirect(routes.Countries.edit(id)).flashing("success" -> "Country %s has been updated".format(country.name))
          //Redirect(routes.Countries.view(country.id))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      Ok(views.html.countries.create("New Country", countryForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>

      countryForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.countries.create("New Country - errors", formWithErrors)),
        country => {
          models.Countries.insert(country)
          Redirect(routes.Countries.create).flashing("success" -> "Country %s has been created".format(country.name))
          //Redirect(routes.Countries.view(country.id))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      models.Countries.delete(id)
      Home.flashing("success" -> "Country has been deleted")
  }

}