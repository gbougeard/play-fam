package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Answer
import models.Answers._

import play.api.libs.json._
import play.api.Logger

import metrics.Instrumented

object Answers extends Controller with Instrumented {
  private[this] val timer = metrics.timer("count")



  /**
   * This result directly redirect to the application home.
   */
  //  val Home = Redirect(routes.Answers.list(0, 0))

  /**
   * Describe the answer form (used in both edit and create screens).
   */
  val answerForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "eventId" -> longNumber,
      "playerId" -> longNumber,
      "typAnswerId" -> longNumber,
      "comments" -> optional(text)
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Answer.apply)(Answer.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  //  def index = Action {
  //    Home
  //  }

  //  def list = Action {
  //    val answers = models.Answers.findAll
  //    val html = views.html.answers("Liste des answers", answers)
  //    Ok(html)
  //  }

  //  def list(page: Int, orderBy: Int) = Action {
  //    implicit request =>
  //      val answers = timer.time(models.Answers.findPage(page, orderBy))
  //      val html = views.html.answers.list("Liste des answers", answers, orderBy)
  //      Ok(html)
  //  }

  def byEvent(id: Long) = Action {
    implicit request =>
      models.Events.findById(id).map {
        event =>
        val answers = models.Answers.findByEvent(id)
        Ok(views.html.answers.view("View Answers", event, answers))
      } getOrElse (NotFound)

  }

  def jsonByEvent(id: Long) = Action {
    implicit request =>
      models.Events.findById(id).map {
        event =>
        val answers = models.Answers.findByEvent(id)
          Ok(Json.toJson(answers))
      } getOrElse (NotFound)

  }

  //  def view(id: Long) = Action {
  //    implicit request =>
  //      models.Answers.findById(id).map {
  //        answer => Ok(views.html.answers.view("View Answer", answer))
  //      } getOrElse (NotFound)
  //  }
  //
  //  def edit(id: Long) = Action {
  //    implicit request =>
  //      models.Answers.findById(id).map {
  //        case (answer, typAnswer, answerStatus) => Ok(views.html.answers.edit("Edit Answer", id, answerForm.fill(answer)))
  //      } getOrElse (NotFound)
  //  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  //  def update(id: Long) = Action {
  //    implicit request =>
  //      answerForm.bindFromRequest.fold(
  //        formWithErrors => BadRequest(views.html.answers.edit("Edit Answer - errors", id, formWithErrors)),
  //        answer => {
  //          models.Answers.update(id, answer)
  //          //        Home.flashing("success" -> "Answer %s has been updated".format(answer.name))
  //          Redirect(routes.Answers.list(0, 2))
  //        }
  //      )
  //  }

  /**
   * Display the 'new computer form'.
   */
  //  def create = Action {
  //    implicit request =>
  //      Ok(views.html.answers.create("New Answer", answerForm))
  //  }

  /**
   * Handle the 'new computer form' submission.
   */
  //  def save = Action {
  //    implicit request =>
  //      answerForm.bindFromRequest.fold(
  //        formWithErrors => BadRequest(views.html.answers.create("New Answer - errors", formWithErrors)),
  //        answer => {
  //          models.Answers.insert(answer)
  //          //        Home.flashing("success" -> "Answer %s has been created".format(answer.name))
  //          Redirect(routes.Answers.list(0, 2))
  //        }
  //      )
  //  }

  /**
   * Handle computer deletion.
   */
  //  def delete(id: Long) = Action {
  //    implicit request =>
  //      models.Answers.delete(id)
  //      Home.flashing("success" -> "Answer has been deleted")
  //  }


}