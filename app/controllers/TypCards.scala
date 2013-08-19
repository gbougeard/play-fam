package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.TypCard


object TypCards extends Controller {


  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.TypCards.list(0, 0))

  /**
   * Describe the typCard form (used in both edit and create screens).
   */
  val typCardForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (TypCard.apply)(TypCard.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val typCards = models.TypCards.findAll
  //    val html = views.html.typCards("Liste des typCards", typCards)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val typCards = models.TypCards.findPage(page, orderBy)
      val html = views.html.typCards.list("Liste des typCards", typCards, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.TypCards.findById(id).map {
        typCard => Ok(views.html.typCards.view("View TypCard", typCard))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.TypCards.findById(id).map {
        typCard => Ok(views.html.typCards.edit("Edit TypCard", id, typCardForm.fill(typCard)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      typCardForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typCards.edit("Edit TypCard - errors", id, formWithErrors)),
        typCard => {
          models.TypCards.update(id, typCard)
          //        Home.flashing("success" -> "TypCard %s has been updated".format(typCard.name))
          //Redirect(routes.TypCards.list(0, 2))
          Redirect(routes.TypCards.view(id)).flashing("success" -> "TypCard %s has been updated".format(typCard.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.typCards.create("New TypCard", typCardForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      typCardForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typCards.create("New TypCard - errors", formWithErrors)),
        typCard => {
          models.TypCards.insert(typCard)
          //        Home.flashing("success" -> "TypCard %s has been created".format(typCard.name))
          Redirect(routes.TypCards.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.TypCards.delete(id)
      Home.flashing("success" -> "TypCard has been deleted")
  }

}