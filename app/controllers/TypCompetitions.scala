package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{TypCompetition, TypMatch}
import metrics.Instrumented


object TypCompetitions extends Controller with Instrumented{

  private[this] val timer = metrics.timer("count")

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
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val typCompetitions = models.TypCompetitions.findAll
  //    val html = views.html.typCompetitions("Liste des typCompetitions", typCompetitions)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val typCompetitions = timer.time(models.TypCompetitions.findPage(page, orderBy))
      val html = views.html.typCompetitions.list("Liste des typCompetitions", typCompetitions, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.TypCompetitions.findById(id).map {
        typMatch => Ok(views.html.typCompetitions.view("View TypMatch", typMatch))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.TypCompetitions.findById(id).map {
        typMatch => Ok(views.html.typCompetitions.edit("Edit TypMatch", id, typMatchForm.fill(typMatch), models.TypMatches.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      typMatchForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typCompetitions.edit("Edit TypMatch - errors", id, formWithErrors, models.TypMatches.options)),
        typMatch => {
          models.TypCompetitions.update(id, typMatch)
          //        Home.flashing("success" -> "TypMatch %s has been updated".format(typMatch.name))
          //Redirect(routes.TypCompetitions.list(0, 2))
          Redirect(routes.TypCompetitions.view(id)).flashing("success" -> "TypMatch %s has been updated".format(typMatch.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.typCompetitions.create("New TypMatch", typMatchForm, models.TypMatches.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      typMatchForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.typCompetitions.create("New TypMatch - errors", formWithErrors, models.TypMatches.options)),
        typMatch => {
          models.TypCompetitions.insert(typMatch)
          //        Home.flashing("success" -> "TypMatch %s has been created".format(typMatch.name))
          Redirect(routes.TypCompetitions.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.TypCompetitions.delete(id)
      Home.flashing("success" -> "TypMatch has been deleted")
  }

}