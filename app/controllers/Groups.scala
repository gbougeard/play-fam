package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Group


object Groups extends Controller {


  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Groups.list(0, 0))

  /**
   * Describe the group form (used in both edit and create screens).
   */
  val groupForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Group.apply)(Group.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val groups = models.Groups.findAll
  //    val html = views.html.groups("Liste des groups", groups)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val groups = models.Groups.findPage(page, orderBy)
      val html = views.html.groups.list("Liste des groups", groups, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Groups.findById(id).map {
        group => Ok(views.html.groups.view("View Group", group))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Groups.findById(id).map {
        group => Ok(views.html.groups.edit("Edit Group", id, groupForm.fill(group)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      groupForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.groups.edit("Edit Group - errors", id, formWithErrors)),
        group => {
          models.Groups.update(id, group)
          //        Home.flashing("success" -> "Group %s has been updated".format(group.name))
          //Redirect(routes.Groups.list(0, 2))
          Redirect(routes.Groups.view(id)).flashing("success" -> "Group %s has been updated".format(group.name))

        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.groups.create("New Group", groupForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      groupForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.groups.create("New Group - errors", formWithErrors)),
        group => {
          models.Groups.insert(group)
          //        Home.flashing("success" -> "Group %s has been created".format(group.name))
          Redirect(routes.Groups.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.Groups.delete(id)
      Home.flashing("success" -> "Group has been deleted")
  }

}