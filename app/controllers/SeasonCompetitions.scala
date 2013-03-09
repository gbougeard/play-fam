package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import models.SeasonCompetition


object SeasonCompetitions extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[SeasonCompetition], "page")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.SeasonCompetitions.list(0, 0))

  /**
   * Describe the seasonCompetition form (used in both edit and create screens).
   */
  val seasonCompetitionForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "categoryId" -> longNumber,
      "scaleId" -> longNumber ,
      "typCompetitionId" -> longNumber
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (SeasonCompetition.apply)(SeasonCompetition.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val seasonCompetitions = models.SeasonCompetitions.findAll
  //    val html = views.html.seasonCompetitions("Liste des seasonCompetitions", seasonCompetitions)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val seasonCompetitions = timer.time(models.SeasonCompetitions.findPage(page, orderBy))
      val html = views.html.seasonCompetitions.list("Liste des seasonCompetitions", seasonCompetitions, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.SeasonCompetitions.findById(id).map {
        seasonCompetition => Ok(views.html.seasonCompetitions.view("View SeasonMatch", seasonCompetition))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.SeasonCompetitions.findById(id).map {
        seasonCompetition => Ok(views.html.seasonCompetitions.edit("Edit SeasonMatch", id, seasonCompetitionForm.fill(seasonCompetition)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      seasonCompetitionForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.seasonCompetitions.edit("Edit SeasonMatch - errors", id, formWithErrors)),
        seasonCompetition => {
          models.SeasonCompetitions.update(id, seasonCompetition)
          //        Home.flashing("success" -> "SeasonMatch %s has been updated".format(seasonCompetition.name))
          //Redirect(routes.SeasonCompetitions.list(0, 2))
          Redirect(routes.SeasonCompetitions.view(id)).flashing("success" -> "SeasonMatch %s has been updated".format(seasonCompetition.id))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.seasonCompetitions.create("New SeasonMatch", seasonCompetitionForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      seasonCompetitionForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.seasonCompetitions.create("New SeasonMatch - errors", formWithErrors)),
        seasonCompetition => {
          models.SeasonCompetitions.insert(seasonCompetition)
          //        Home.flashing("success" -> "SeasonMatch %s has been created".format(seasonCompetition.name))
          Redirect(routes.SeasonCompetitions.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.SeasonCompetitions.delete(id)
      Home.flashing("success" -> "SeasonMatch has been deleted")
  }

}