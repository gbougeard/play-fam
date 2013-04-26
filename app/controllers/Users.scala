package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.User

object Users extends Controller {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Users.list(0, 0))


  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val users = models.Users.findPage(page, orderBy)
      val html = views.html.users.list("Liste des users", users, orderBy)
      Ok(html)
  }

  def view(id: Long) = TODO


  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = TODO


}