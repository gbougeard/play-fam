package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Category

import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer


object Categories extends Controller {

  val metric = Metrics.defaultRegistry().newTimer(classOf[Category], "page")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Categories.list(0, 0))

  /**
   * Describe the category form (used in both edit and create screens).
   */
  val categoryForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Category.apply)(Category.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val categories = models.Categorys.findAll
  //    val html = views.html.categories("Liste des categories", categories)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val categories = timer.time(models.Categorys.findPage(page, orderBy))
      val html = views.html.categories.list("List categories", categories, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Categorys.findById(id).map {
        category => Ok(views.html.categories.view("View Category", category))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Categorys.findById(id).map {
        category => Ok(views.html.categories.edit("Edit Category", id, categoryForm.fill(category)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      categoryForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.categories.edit("Edit Category - errors", id, formWithErrors)),
        category => {
          models.Categorys.update(id, category)
          //        Home.flashing("success" -> "Category %s has been updated".format(category.name))
          //Redirect(routes.Categorys.list(0, 2))
          Redirect(routes.Categories.view(id)).flashing("success" -> "Category %s has been updated".format(category.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.categories.create("New Category", categoryForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      categoryForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.categories.create("New Category - errors", formWithErrors)),
        category => {
          models.Categorys.insert(category)
          //        Home.flashing("success" -> "Category %s has been created".format(category.name))
          Redirect(routes.Categories.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.Categorys.delete(id)
      Home.flashing("success" -> "Category has been deleted")
  }

}