package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Scale

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer


object Scales extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[Scale], "page")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Scales.list(0, 0))

  /**
   * Describe the scale form (used in both edit and create screens).
   */
  val scaleForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "ptsDefeat" -> number,
      "ptsDraw" -> number,
      "ptsVictory" -> number
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Scale.apply)(Scale.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val scales = models.Scales.findAll
  //    val html = views.html.scales("Liste des scales", scales)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val scales = timer.time(models.Scales.findPage(page, orderBy))
      val html = views.html.scales.list("Liste des scales", scales, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Scales.findById(id).map {
        scale => Ok(views.html.scales.view("View Scale", scale))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Scales.findById(id).map {
        scale => Ok(views.html.scales.edit("Edit Scale", id, scaleForm.fill(scale)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      scaleForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.scales.edit("Edit Scale - errors", id, formWithErrors)),
        scale => {
          models.Scales.update(id, scale)
          //        Home.flashing("success" -> "Scale %s has been updated".format(scale.name))
          //Redirect(routes.Scales.list(0, 2))
          Redirect(routes.Scales.view(id)).flashing("success" -> "Scale %s has been updated".format(scale.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.scales.create("New Scale", scaleForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      scaleForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.scales.create("New Scale - errors", formWithErrors)),
        scale => {
          models.Scales.insert(scale)
          //        Home.flashing("success" -> "Scale %s has been created".format(scale.name))
          Redirect(routes.Scales.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.Scales.delete(id)
      Home.flashing("success" -> "Scale has been deleted")
  }

}