package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import service.Administrator


object TypCompetitions extends Controller with securesocial.core.SecureSocial{

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.TypCompetitions.list(0, 0))

  /**
   * Describe the typMatch form (used in both edit and create screens).
   */
  val typMatchForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "isChampionship" -> checked("is championship"),
      "typMatchId" -> longNumber
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (TypCompetition.apply)(TypCompetition.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val typCompetitions = TypCompetition.findPage(page, orderBy)
      val html = views.html.typCompetitions.list("Liste des typCompetitions", typCompetitions, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      TypCompetition.findById(id).map {
        typMatch => Ok(views.html.typCompetitions.view("View TypMatch", typMatch))
      } getOrElse (NotFound)
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      TypCompetition.findById(id).map {
        typMatch => Ok(views.html.typCompetitions.edit("Edit TypMatch", id, typMatchForm.fill(typMatch), TypMatch.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      typMatchForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typCompetitions.edit("Edit TypMatch - errors", id, formWithErrors, TypMatch.options)),
        typMatch => {
          TypCompetition.update(id, typMatch)
          //        Home.flashing("success" -> "TypMatch %s has been updated".format(typMatch.name))
          //Redirect(routes.TypCompetitions.list(0, 2))
          Redirect(routes.TypCompetitions.view(id)).flashing("success" -> "TypMatch %s has been updated".format(typMatch.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      Ok(views.html.typCompetitions.create("New TypMatch", typMatchForm, TypMatch.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      typMatchForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typCompetitions.create("New TypMatch - errors", formWithErrors, TypMatch.options)),
        typMatch => {
          TypCompetition.insert(typMatch)
          //        Home.flashing("success" -> "TypMatch %s has been created".format(typMatch.name))
          Redirect(routes.TypCompetitions.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      TypCompetition.delete(id)
      Home.flashing("success" -> "TypMatch has been deleted")
  }

}