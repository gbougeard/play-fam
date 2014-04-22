package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json

import models._
import models.PageJson._
import models.CategoryJson._

import service.{Administrator, Coach}


object Categories extends Controller with securesocial.core.SecureSocial {

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

  def listAll = Action {
    implicit request =>
      render {
        case Accepts.Json() =>
          val categories = models.Categories.findAll
          Ok(Json.toJson(categories))
      }
  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val categories = models.Categories.findPage(page, orderBy)
      render {
        case Accepts.Html() => Ok(views.html.categories.list("List categories", categories, orderBy))
        case Accepts.Json() => Ok(Json.toJson(categories))
      }
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Categories.findById(id).map {
        category =>
          render {
            case Accepts.Html() => Ok(views.html.categories.view("View Category", category))
            case Accepts.Json() => Ok(Json.toJson(category))
          }
      } getOrElse NotFound
  }

  def edit(id: Long) = SecuredAction(WithRoles(Set(Coach))) {
    implicit request =>
      models.Categories.findById(id).map {
        category => Ok(views.html.categories.edit("Edit Category", id, categoryForm.fill(category)))
      } getOrElse NotFound
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = SecuredAction(WithRoles(Set(Coach))) {
    implicit request =>
      categoryForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.categories.edit("Edit Category - errors", id, formWithErrors)),
        category => {
          models.Categories.update(id, category)
          //        Home.flashing("success" -> "Category %s has been updated".format(category.name))
          //Redirect(routes.Categorys.list(0, 2))
          Redirect(routes.Categories.view(id)).flashing("success" -> "Category %s has been updated".format(category.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = SecuredAction(WithRoles(Set(Coach))) {
    implicit request =>
      Ok(views.html.categories.create("New Category", categoryForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = SecuredAction(WithRoles(Set(Coach))) {
    implicit request =>
      categoryForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.categories.create("New Category - errors", formWithErrors)),
        category => {
          models.Categories.insert(category)
          //        Home.flashing("success" -> "Category %s has been created".format(category.name))
          Redirect(routes.Categories.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = SecuredAction(WithRoles(Set(Administrator))) {
    implicit request =>
      models.Categories.delete(id)
      Home.flashing("success" -> "Category has been deleted")
  }

}