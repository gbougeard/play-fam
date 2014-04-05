package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import service.{Administrator, Coach}

object Users extends Controller with securesocial.core.SecureSocial {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Users.list(0, 0))


  // -- Actions

  def list(page: Int, orderBy: Int) = SecuredAction(WithRoles(Set(Administrator))) {
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