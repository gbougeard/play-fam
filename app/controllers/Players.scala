package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Player
import models.common.{AppDB, DBeable}
import models.common.JsonImplicits._
import slick.session.Session

import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._


object Players extends Controller {

  //implicit val playerFormat = Json.format[Player]

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Players.list(0, 0))

  lazy val database = AppDB.database
  lazy val dal = AppDB.dal

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
      database.withSession {
        implicit session: Session =>
          val players = dal.Players.findPage(page, orderBy)
          val html = views.html.players.list("Liste des players", players, orderBy)
          Ok(html)
      }
  }

  def view(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.Players.findById(id).map {
            player => Ok(views.html.players.view("View Player", player))
          } getOrElse (NotFound)
      }
  }

  def edit(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          dal.Players.findById(id).map {
            player => Ok(views.html.players.edit("Edit Player", id, playerForm.fill(player)))
          } getOrElse (NotFound)
      }
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          playerForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.players.edit("Edit Player - errors", id, formWithErrors)),
            player => {
              dal.Players.update(player)
              //        Home.flashing("success" -> "Player %s has been updated".format(player.name))
              Redirect(routes.Players.list(0, 2))
            }
          )
      }
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    Ok(views.html.players.create("New Player", playerForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          playerForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.html.players.create("New Player - errors", formWithErrors)),
            player => {
              dal.Players.insert(player)
              //        Home.flashing("success" -> "Player %s has been created".format(player.name))
              Redirect(routes.Players.list(0, 2))
            }
          )
      }
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    database.withSession {
      implicit session: Session =>
        dal.Players.delete(id)
        Home.flashing("success" -> "Player has been deleted")
    }
  }

  def json(id: Long) = Action {
    database.withSession {
      implicit session: Session =>
        dal.Players.findById(id) map {
          player => Ok(Json.toJson(player))
        } getOrElse (NotFound)
    }
  }

}