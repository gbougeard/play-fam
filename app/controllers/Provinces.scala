package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import service.{Administrator, Coach}


object Provinces extends Controller with securesocial.core.SecureSocial {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Provinces.list(0, 0))

  /**
   * Describe the province form (used in both edit and create screens).
   */
  val provinceForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "upper" -> nonEmptyText,
      "lower" -> nonEmptyText,
      "stateId" -> longNumber
    )
      (Province.apply)(Province.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val provinces = Province.findPage(page, orderBy)
      val html = views.html.provinces.list("Liste des provinces", provinces, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      Province.findById(id).map {
        province => Ok(views.html.provinces.view("View Province", province))
      } getOrElse (NotFound)
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      Province.findById(id).map {
        province => Ok(views.html.provinces.edit("Edit Province", id, provinceForm.fill(province), State.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      provinceForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.provinces.edit("Edit Province - errors", id, formWithErrors, State.options)),
        province => {

          Province.update(id, province)
          Redirect(routes.Provinces.edit(id)).flashing("success" -> "Province %s has been updated".format(province.name))
          //Redirect(routes.Provinces.view(province.id))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      Ok(views.html.provinces.create("New Province", provinceForm, State.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      provinceForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.provinces.create("New Province - errors", formWithErrors, State.options)),
        province => {
          Province.insert(province)
          Redirect(routes.Provinces.create).flashing("success" -> "Province %s has been created".format(province.name))
          // Redirect(routes.Provinces.view(province.id))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      Province.delete(id)
      Home.flashing("success" -> "Province has been deleted")
  }

}