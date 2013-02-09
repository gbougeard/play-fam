package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.User

import play.api.Play.current
import slick.session.Session


object Users extends Controller {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Users.list(0, 0))

  /**
   * Describe the user form (used in both edit and create screens).
   */
  val userForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "email" -> email
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (User.apply)(User.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val users = models.Users.findAll
  //    val html = views.html.users("Liste des users", users)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val users = models.Users.findPage(page, orderBy)
      val html = views.html.users.list("Liste des users", users, orderBy)
      Ok(html)
  }

  def view(id: Long) = TODO

  /*Action {
      implicit request =>
        models.Users.findById(id).map {
          user => Ok(views.html.users.view("View User", user))
        } getOrElse (NotFound)
    } */

  def edit(id: Long) = TODO

  /*Action {
      implicit request =>
        models.Users.findById(id).map {
          user => Ok(views.html.users.edit("Edit User", id, userForm.fill(user)))
        } getOrElse (NotFound)
    }*/

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = TODO

  /*Action {
      implicit request =>
        userForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.users.edit("Edit User - errors", id, formWithErrors)),
          user => {
            models.Users.update(user)
            //        Home.flashing("success" -> "User %s has been updated".format(user.name))
            Redirect(routes.Users.list(0, 2))
          }
        )
    }   */

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
      Ok(views.html.users.create("New User", userForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = TODO

  /*Action {
      implicit request =>
        userForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.users.create("New User - errors", formWithErrors)),
          user => {
            models.Users.insert(user)
            //        Home.flashing("success" -> "User %s has been created".format(user.name))
            Redirect(routes.Users.list(0, 2))
          }
        )
    }   */

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = TODO

  /*Action {
      models.Users.delete(id)
      Home.flashing("success" -> "User has been deleted")
    }  */

}