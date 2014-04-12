package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

import json.ImplicitGlobals._
import service.Administrator



object TypMatches extends Controller with securesocial.core.SecureSocial {


  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.TypMatches.list(0, 0))

  /**
   * Describe the typMatch form (used in both edit and create screens).
   */
  val typMatchForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "nbSubstitute" -> number(min=0, max=12),
      "nbPlayer" -> number(min=5, max=11),
      "periodDuration"-> number(min=0, max=45),
      "hasExtraTime" -> checked("extratime"),
      "extraTimeDuration" -> optional(number(min=0, max=15)),
      "hasInfiniteSubs" -> checked("infinite subsitutions"),
      "nbSubstitution" -> optional(number),
      "hasPenalties" -> checked("penalties"),
      "nbPenalties" -> optional(number(min=0,max=5))
    )
      (TypMatch.apply)(TypMatch.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val typMatches = models.TypMatches.findPage(page, orderBy)
      val html = views.html.typMatches.list("Liste des typMatches", typMatches, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.TypMatches.findById(id).map {
        typMatch => Ok(views.html.typMatches.view("View TypMatch", typMatch))
      } getOrElse NotFound
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      models.TypMatches.findById(id).map {
        typMatch => Ok(views.html.typMatches.edit("Edit TypMatch", id, typMatchForm.fill(typMatch)))
      } getOrElse NotFound
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      typMatchForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typMatches.edit("Edit TypMatch - errors", id, formWithErrors)),
        typMatch => {
          models.TypMatches.update(id, typMatch)
          //        Home.flashing("success" -> "TypMatch %s has been updated".format(typMatch.name))
          //Redirect(routes.TypMatches.list(0, 2))
          Redirect(routes.TypMatches.view(id)).flashing("success" -> "TypMatch %s has been updated".format(typMatch.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      Ok(views.html.typMatches.create("New TypMatch", typMatchForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      typMatchForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typMatches.create("New TypMatch - errors", formWithErrors)),
        typMatch => {
          models.TypMatches.insert(typMatch)
          //        Home.flashing("success" -> "TypMatch %s has been created".format(typMatch.name))
          Redirect(routes.TypMatches.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      models.TypMatches.delete(id)
      Home.flashing("success" -> "TypMatch has been deleted")
  }

}