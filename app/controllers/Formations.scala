package controllers

import play.api.Logger
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

import json.ImplicitGlobals._
import play.api.libs.json.{JsError, Json}
import service.{Administrator, Coach}



object Formations extends Controller  with securesocial.core.SecureSocial{

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Formations.list(0, 0))

  /**
   * Describe the formation form (used in both edit and create screens).
   */
  val formationForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "isDefault" -> boolean,
      "typMatchId" -> longNumber
    )
      (Formation.apply)(Formation.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val formations = models.Formations.findPage(page, orderBy)
      val html = views.html.formations.list("Liste des formations", formations, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Formations.findById(id).map {
        formation => Ok(views.html.formations.view("View Formation", formation, models.FormationItems.findByFormation(id)))
      } getOrElse NotFound
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      models.Formations.findById(id).map {
        formation =>
          val items = models.FormationItems.findByFormation(id).sortBy(_.numItem)
          Ok(views.html.formations.edit("Edit Formation", id, formationForm.fill(formation), items, Json.toJson(items).toString(), models.TypMatches.options))
      } getOrElse NotFound
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      val items = models.FormationItems.findByFormation(id).sortBy(_.numItem)
      Logger.debug(items.toString())
      formationForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.formations.edit("Edit Formation - errors", id, formWithErrors, items, Json.toJson(items).toString(),models.TypMatches.options)),
        formation => {
          models.Formations.update(id, formation)
          //        Home.flashing("success" -> "Formation %s has been updated".format(formation.name))
          Redirect(routes.Formations.list(0, 2))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      Ok(views.html.formations.create("New Formation", formationForm,models.Clubs.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      formationForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.formations.create("New Formation - errors", formWithErrors,models.Clubs.options)),
        formation => {
          models.Formations.insert(formation)
          //        Home.flashing("success" -> "Formation %s has been created".format(formation.name))
          Redirect(routes.Formations.list(0, 2))
        }
      )
  }

  def saveItems = Action(parse.json) {
    implicit request =>
      val itemsJson = request.body
      Logger.debug(itemsJson.toString())
      itemsJson.validate[Seq[FormationItem]].map {
        case items => models.FormationItems.save(items)
        Ok("Saved")
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      models.Formations.delete(id)
      Home.flashing("success" -> "Formation has been deleted")
  }

}