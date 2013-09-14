package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Club
import service.Coach
import service.Administrator
import play.api.libs.json._


object Clubs extends Controller with securesocial.core.SecureSocial {

  case class FffClub(code: String, name: String, colours: String, address: String, orga1: String, orga2: String)

  implicit val fffClubRead = Json.format[FffClub]


  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Clubs.list(0, 1))

  /**
   * Describe the club form (used in both edit and create screens).
   */
  val clubForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> number,
      "name" -> nonEmptyText,
      "countryId" -> optional(longNumber),
      "cityId" -> optional(longNumber),
      "colours" -> optional(text),
      "address" -> optional(text),
      "zipcode" -> optional(text),
      "city" -> optional(text),
      "organization" -> optional(longNumber),
      "comments" -> optional(text)
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
      } getOrElse NotFound
  }

  def edit(id: Long) = SecuredAction(WithRightClub(id)) {
    implicit request =>
      models.Clubs.findById(id).map {
        club => Ok(views.html.clubs.edit("Edit Club", id, clubForm.fill(club)))
      } getOrElse NotFound
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = SecuredAction(WithRightClub(id)) {
    implicit request =>
      play.Logger.debug(s"update club $id")
      clubForm.bindFromRequest.fold(
        formWithErrors => {
          play.Logger.debug(s"errors club $id $formWithErrors ")
          BadRequest(views.html.clubs.edit("Edit Club - errors", id, formWithErrors))
        },
        club => {
          play.Logger.debug(s"update club $id $club ")
          models.Clubs.update(id, club)
                  Home.flashing("success" -> "Club %s has been updated".format(club.name))
          //Redirect(routes.Clubs.list(0, 2))
//          Redirect(routes.Clubs.view(id)).flashing("success" -> "Club %s has been updated".format(club.name))

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
      play.Logger.debug(s"save club")
      clubForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.clubs.create("New Club - errors", formWithErrors)),
        club => {
          models.Clubs.insert(club)
                  Home.flashing("success" -> "Club %s has been created".format(club.name))
//          Redirect(routes.Clubs.list(0, 2))
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

  def load = Action {
    import scala.io.Source

    val is = Application.getClass.getResourceAsStream("/public/data/clubs_light.json")
    val src = Source.fromInputStream(is)
    val iter = src.getLines
    val lines = src.mkString
    src.close()
    val fffClubs = Json.parse(lines).as[Seq[FffClub]]
    fffClubs.map {
      fffClub =>
        val adr =  if (fffClub.address.split(" - ").length > 2) fffClub.address.split(" - ").reverse.tail.tail.head else fffClub.address
        val zipcode = if (fffClub.address.split(" - ").length > 2) fffClub.address.split(" - ").reverse.tail.head  else ""
        val city = if (fffClub.address.split(" - ").length > 2) fffClub.address.split(" - ").reverse.head  else ""

        val col = fffClub.colours match {
          case "" => None
          case s:String  => Some(s.trim)
        }

        val club = Club(code = fffClub.code.trim.toInt,
          name = fffClub.name.trim,
          colours = col,
          comments = Some(Json.toJson(fffClub).toString),
          address = adr match {
          case "" => None
          case s:String  => Some(s.trim)
        },
          zipcode = zipcode match {
          case "" => None
          case s:String  => Some(s.trim)
        },
          city =city match {
          case "" => None
          case s:String  => Some(s.trim)
        })
        play.Logger.debug(s"$club")
              models.Clubs.insert(club)
    }

    Ok
  }

}