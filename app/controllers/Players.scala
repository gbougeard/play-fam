package controllers

import _root_.securesocial.core.Authorization
import _root_.securesocial.core.Identity

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Player

import play.api.Logger
import service.Administrator

object Players extends Controller with securesocial.core.SecureSocial{


  /**
   * Describe the player form (used in both edit and create screens).
   */
  val playerForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> nonEmptyText,
            "userId" -> optional(longNumber)
    )
      (Player.apply)(Player.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = SecuredAction {
    implicit request =>
      val players = models.Players.findPage(page, orderBy)
      val html = views.html.players.list("Liste des players", players, orderBy)
      Ok(html)
  }

  def view(id: Long) = SecuredAction {
    implicit request =>
      models.Players.findById(id).map {
        player => Ok(views.html.players.view("View Player", player))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = SecuredAction {
    implicit request =>
      models.Players.findById(id).map {
        player => Ok(views.html.players.edit("Edit Player", id, playerForm.fill(player)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction  {
    implicit request =>
      Logger.info("update player "+id)
      playerForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.players.edit("Edit Player - errors", id, formWithErrors)),
        player => {

          models.Players.update(id, player)
          Redirect(routes.Players.edit(id)).flashing("success" -> "Player %s has been updated".format(player.firstName + " " + player.lastName))
          //          Redirect(routes.Players.list(0, 2))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = SecuredAction {
    implicit request =>
      Ok(views.html.players.create("New Player", playerForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = SecuredAction {
    implicit request =>
      playerForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.players.create("New Player - errors", formWithErrors)),
        player => {
          models.Players.insert(player)
          //        Home.flashing("success" -> "Player %s has been created".format(player.name))
          Redirect(routes.Players.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      models.Players.delete(id)
      Redirect(routes.Players.list(0,0)).flashing("success" -> "Player has been deleted")
  }

}