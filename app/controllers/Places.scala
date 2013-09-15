package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{ Event, Place}
import models.Places._
import slick.session.Session
import play.api.libs.json._
import play.api.libs.functional.syntax._
import service.{Coach,Administrator}
import java.net.URLEncoder
import play.api.libs.ws.WS
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._


object Places extends Controller  with securesocial.core.SecureSocial{

  case class FffPlace(code: String, typ: String, name: String, address: String)

  implicit val fffPlaceRead = Json.format[FffPlace]

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Places.list(0, 0))

  /**
   * Describe the place form (used in both edit and create screens).
   */
  val placeForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "city" -> nonEmptyText,
      "zipcode" -> number,
      "latitude" -> optional(ignored(0.0f)),
      "longitude" -> optional(ignored(0.0f)),
      "comments" -> optional(text),
      "typFff" -> optional(text)
    )
      (Place.apply)(Place.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val places = models.Places.findPage(page, orderBy)
      val html = views.html.places.list("Liste des places", places, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Places.findById(id).map {
        place => Ok(views.html.places.view("View Place", place))
      } getOrElse (NotFound)
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      models.Places.findById(id).map {
        place => Ok(views.html.places.edit("Edit Place", id, placeForm.fill(place)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
//  def update(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
//    implicit request =>
//      placeForm.bindFromRequest.fold(
//        formWithErrors => BadRequest(views.html.places.edit("Edit Place - errors", id, formWithErrors)),
//        place => {
//          play.Logger.debug(s"update Place $place")
//          models.Places.update(id, place)
//          //        Home.flashing("success" -> "Place %s has been updated".format(place.name))
//          Redirect(routes.Places.list(0, 2))
//        }
//      )
//  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      Ok(views.html.places.create("New Place", placeForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
//  def save =  SecuredAction(WithRoles(Set(Coach)))  {
//    implicit request =>
//      placeForm.bindFromRequest.fold(
//        formWithErrors => BadRequest(views.html.places.create("New Place - errors", formWithErrors)),
//        place => {
//          models.Places.insert(place)
//          //        Home.flashing("success" -> "Place %s has been created".format(place.name))
//          Redirect(routes.Places.list(0, 2))
//        }
//      )
//  }

  def update(id: Long) = Action(parse.json) {
    implicit request =>
      val json = request.body
      val place = json.as[Place]
      models.Places.update(id, place)
      Ok(Json.toJson(id))
  }

  def save() = Action(parse.json) {
    implicit request =>
      val json = request.body
      val place = json.as[Place]
      val id = models.Places.insert(place)
      Ok(Json.toJson(id))
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      models.Places.delete(id)
      Home.flashing("success" -> "Place has been deleted")
  }


  /**
   * Display the 'new computer form'.
   */
  def gmap = Action {
    implicit request =>
      Ok(views.html.places.map("Map des places"))
  }

  def gmapData = Action {
    implicit request =>
      val places = models.Places.placesWithCoords
      Ok(Json.toJson(places))
  }

  def jsonList = Action {
    implicit request =>
      Ok(Json.toJson(models.Places.findAll))
  }

  def jsonById(id: Long) = Action {
    implicit request =>
      models.Places.findById(id).map {
        place => Ok(Json.toJson(place))
      } getOrElse (NotFound)
  }

  def load = Action {
    import scala.io.Source

    val is = Application.getClass.getResourceAsStream("/public/data/terrains_light.json")
    val src = Source.fromInputStream(is)
    val lines = src.mkString
    src.close()
    val fffPlaces = Json.parse(lines).as[Seq[FffPlace]]
    fffPlaces.map {
      fffPlace =>
        val adr =  fffPlace.address.split(" - ").head //if (fffPlace.address.split(" - ").length > 1) fffPlace.address.split(" - ").reverse.tail.head else fffPlace.address
        val zipcode = fffPlace.address.split(" - ").reverse.head.split(' ').head//if (fffPlace.address.split(" - ").length > 1) fffPlace.address.split(" - ").reverse.head.split(' ').head  else ""
        val city = fffPlace.address.split(" - ").reverse.head.split(' ').reverse.head //if (fffPlace.address.split(" - ").length > 1) fffPlace.address.split(" - ").reverse.head.split(' ').tail  else ""

//      play.Logger.debug(s"adress ${fffPlace.address}")
//      play.Logger.debug(s"adr ${fffPlace.address.split(" - ").head}")
//      play.Logger.debug(s"zipcode ${fffPlace.address.split(" - ").reverse.head.split(' ').head}")
//      play.Logger.debug(s"city ${fffPlace.address.split(" - ").reverse.head.split(' ').reverse.head}")

        val place = Place( typFff = Some(fffPlace.typ.trim),
          name = fffPlace.name.trim,
          comments = Some(Json.toJson(fffPlace).toString),
          address = adr.trim,
          zipcode = zipcode.trim.toInt,
          city =city.trim)

        play.Logger.debug(s"$place")
        models.Places.insert(place)
    }

    Ok
  }

  def geo = Action {
    val places = models.Places.findPage(4,1)
      val results = places.items.map{
        place =>
          val latLng = fetchLatitudeAndLongitude(s"${place.address} ${place.zipcode} ${place.city} ")
          latLng match{
            case Some(coord) => {
              val p = place.copy(latitude=Some(coord._1.toFloat), longitude=Some(coord._2.toFloat))
              play.Logger.debug(s"copie $p")
              models.Places.update(place.id.get, p)
            }
            case _ =>
          }
      }

    Ok
  }

  def fetchLatitudeAndLongitude(address: String): Option[(Double, Double)] = {
    implicit val timeout = Timeout(50000 milliseconds)


    play.Logger.info(s"fetchLatitudeAndLongitude $address")

    // Encoded the address in order to remove the spaces from the address (spaces will be replaced by '+')
    //@purpose There should be no spaces in the parameter values for a GET request
    val addressEncoded = URLEncoder.encode(address, "UTF-8");
    val jsonContainingLatitudeAndLongitude = WS.url("http://maps.googleapis.com/maps/api/geocode/json?address=" +
      addressEncoded + "&sensor=true").get()

    val future = jsonContainingLatitudeAndLongitude map {
      response => (response.json \\ "location")
    }

    // Wait until the future completes (Specified the timeout above)

    val result = Await.result(future, timeout.duration).asInstanceOf[List[play.api.libs.json.JsObject]]
     play.Logger.debug(s"promise result $result")
    //Fetch the values for Latitude & Longitude from the result of future
    result.length match{
      case 0 => None
      case _ =>  {
        val latitude = (result(0) \\ "lat")(0).toString.toDouble
        val longitude = (result(0) \\ "lng")(0).toString.toDouble
        Option(latitude, longitude)
      }
    }

  }

}