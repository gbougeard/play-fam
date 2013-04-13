package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Formation


object Formations extends Controller {

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
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Formation.apply)(Formation.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val formations = models.Formations.findAll
  //    val html = views.html.formations("Liste des formations", formations)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val formations = models.Formations.findPage(page, orderBy)
      val html = views.html.formations.list("Liste des formations", formations, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Formations.findById(id).map {
        formation => Ok(views.html.formations.view("View Formation", formation))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Formations.findById(id).map {
        formation => Ok(views.html.formations.edit("Edit Formation", id, formationForm.fill(formation), models.TypMatches.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      formationForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.formations.edit("Edit Formation - errors", id, formWithErrors, models.TypMatches.options)),
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
  def create = Action {
    implicit request =>
      Ok(views.html.formations.create("New Formation", formationForm, models.Clubs.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      formationForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.formations.create("New Formation - errors", formWithErrors, models.Clubs.options)),
        formation => {
          models.Formations.insert(formation)
          //        Home.flashing("success" -> "Formation %s has been created".format(formation.name))
          Redirect(routes.Formations.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.Formations.delete(id)
      Home.flashing("success" -> "Formation has been deleted")
  }

}