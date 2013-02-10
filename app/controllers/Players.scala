package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Player

import play.api.Logger

object Players extends Controller {

  //implicit val playerFormat = Json.format[Player]

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Players.list(0, 0))

  /**
   * Describe the player form (used in both edit and create screens).
   */
  val playerForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Player.apply)(Player.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val players = models.Players.findAll
  //    val html = views.html.players("Liste des players", players)
  //    Ok(html)
  //  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val players = models.Players.findPage(page, orderBy)
      val html = views.html.players.list("Liste des players", players, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Players.findById(id).map {
        player => Ok(views.html.players.view("View Player", player))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
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
  def update(id: Long) = Action {
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
  def create = Action {
    implicit request =>
      Ok(views.html.players.create("New Player", playerForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
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
  def delete(id: Long) = Action {
    implicit request =>
      models.Players.delete(id)
      Home.flashing("success" -> "Player has been deleted")
  }

}