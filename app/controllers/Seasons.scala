package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Season
import models.Seasons._


object Seasons extends Controller {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Seasons.list(0, 0))

  /**
   * Describe the season form (used in both edit and create screens).
   */
  val seasonForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "currentSeason" -> boolean,
      "name" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Season.apply)(Season.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val seasons = models.Seasons.findAll
  //    val html = views.html.seasons("Liste des seasons", seasons)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val seasons = models.Seasons.findPage(page, orderBy)
      val html = views.html.seasons.list("Liste des seasons", seasons, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Seasons.findById(id).map {
        season => Ok(views.html.seasons.view("View Season", season))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Seasons.findById(id).map {
        season => Ok(views.html.seasons.edit("Edit Season", id, seasonForm.fill(season)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      seasonForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.seasons.edit("Edit Season - errors", id, formWithErrors)),
        season => {
          models.Seasons.update(id, season)
          Home.flashing("success" -> "Season %s has been updated".format(season.name))
          //Redirect(routes.Seasons.list(0, 2))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.seasons.create("New Season", seasonForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      seasonForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.seasons.create("New Season - errors", formWithErrors)),
        season => {
          models.Seasons.insert(season)
          //        Home.flashing("success" -> "Season %s has been created".format(season.name))
          Redirect(routes.Seasons.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.Seasons.delete(id)
      Home.flashing("success" -> "Season has been deleted")
  }

}