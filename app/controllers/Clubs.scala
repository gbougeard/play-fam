package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.Logger
import models.Club
import service.Coach
import service.Administrator


object Clubs extends Controller with securesocial.core.SecureSocial  {

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
      "name" -> nonEmptyText,
            "country" -> optional(longNumber),
            "city" -> optional(longNumber)
    )
      (Club.apply)(Club.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val clubs = models.Clubs.findPage(page, orderBy)
      val html = views.html.clubs.list("Liste des clubs", clubs, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Clubs.findById(id).map {
        club => Ok(views.html.clubs.view("View Club", club))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = SecuredAction(WithRightClub(id)) {
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
  def update(id: Long) = SecuredAction(WithRightClub(id)) {
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

  def page = UserAwareAction {
    implicit request =>
    val userName = request.user match {
      case Some(user) => user.fullName
      case _ => "guest"
    }
    Ok("Hello %s".format(userName))
  }

  // you don't want to redirect to the login page for ajax calls so
  // adding a ajaxCall = true will make SecureSocial return a forbidden error
  // instead.
//  def ajaxCall = SecuredAction(ajaxCall = true) {
//    implicit request =>
//    // return some json
//  }



  /**
   * Display the 'new computer form'.
   */
  def create = SecuredAction(WithRoles(Set(Coach))) {
    implicit request =>
      Ok(views.html.clubs.create("New Club", clubForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = SecuredAction(WithRoles(Set(Coach))) {
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
  def delete(id: Long) = SecuredAction(WithRoles(Set(Administrator))) {
    implicit request =>
      models.Clubs.delete(id)
      Home.flashing("success" -> "Club has been deleted")
  }

}