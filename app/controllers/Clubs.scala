package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Club

import play.api.Play.current
import slick.session.Session
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer


object Clubs extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[Club], "page")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Clubs.list(0, 0))

  /**
   * Describe the club form (used in both edit and create screens).
   */
  val clubForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> number,
      "name" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Club.apply)(Club.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val clubs = models.Clubs.findAll
  //    val html = views.html.clubs("Liste des clubs", clubs)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val clubs = timer.time(models.Clubs.findPage(page, orderBy))
      val html = views.html.clubs.list("Liste des clubs", clubs, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Clubs.findById(id).map {
        club => Ok(views.html.clubs.view("View Club", club))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Clubs.findById(id).map {
        club => Ok(views.html.clubs.edit("Edit Club", id, clubForm.fill(club)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      clubForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.clubs.edit("Edit Club - errors", id, formWithErrors)),
        club => {
          models.Clubs.update(id, club)
          //        Home.flashing("success" -> "Club %s has been updated".format(club.name))
          //Redirect(routes.Clubs.list(0, 2))
          Redirect(routes.Clubs.view(id)).flashing("success" -> "Club %s has been updated".format(club.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.clubs.create("New Club", clubForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      clubForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.clubs.create("New Club - errors", formWithErrors)),
        club => {
          models.Clubs.insert(club)
          //        Home.flashing("success" -> "Club %s has been created".format(club.name))
          Redirect(routes.Clubs.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.Clubs.delete(id)
      Home.flashing("success" -> "Club has been deleted")
  }

}