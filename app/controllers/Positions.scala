package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Position

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer


object Positions extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[Position], "page")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Positions.list(0, 0))

  /**
   * Describe the position form (used in both edit and create screens).
   */
  val positionForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Position.apply)(Position.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val positions = models.Positions.findAll
  //    val html = views.html.positions("Liste des positions", positions)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val positions = timer.time(models.Positions.findPage(page, orderBy))
      val html = views.html.positions.list("Liste des positions", positions, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Positions.findById(id).map {
        position => Ok(views.html.positions.view("View Position", position))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Positions.findById(id).map {
        position => Ok(views.html.positions.edit("Edit Position", id, positionForm.fill(position)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      positionForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.positions.edit("Edit Position - errors", id, formWithErrors)),
        position => {
          models.Positions.update(id, position)
          //        Home.flashing("success" -> "Position %s has been updated".format(position.name))
          //Redirect(routes.Positions.list(0, 2))
          Redirect(routes.Positions.view(id)).flashing("success" -> "Position %s has been updated".format(position.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.positions.create("New Position", positionForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      positionForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.positions.create("New Position - errors", formWithErrors)),
        position => {
          models.Positions.insert(position)
          //        Home.flashing("success" -> "Position %s has been created".format(position.name))
          Redirect(routes.Positions.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.Positions.delete(id)
      Home.flashing("success" -> "Position has been deleted")
  }

}