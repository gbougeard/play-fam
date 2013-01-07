package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.City
import models.Cities._
import play.api.libs.json.Json
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer


object Cities extends Controller {
  val metric = Metrics.defaultRegistry().newTimer(classOf[City], "ajax")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Cities.list)
  lazy val citiesCount = models.Cities.count


  /**
   * Describe the city form (used in both edit and create screens).
   */
  val cityForm = Form(
    mapping(
      "id" -> longNumber,
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "upper" -> nonEmptyText,
      "lower" -> nonEmptyText,
      "provinceId" -> longNumber
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (City.apply)(City.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Cities.findById(id).map {
        city => Ok(views.html.cities.view("View City", city))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Cities.findById(id).map {
        city => Ok(views.html.cities.edit("Edit City", id, cityForm.fill(city), models.Provinces.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      cityForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.cities.edit("Edit City - errors", id, formWithErrors, models.Provinces.options)),
        city => {
          models.Cities.update(city)
          //        Home.flashing("success" -> "City %s has been updated".format(city.name))
          Redirect(routes.Cities.view(city.id))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    Ok(views.html.cities.create("New City", cityForm, models.Provinces.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      cityForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.cities.create("New City - errors", formWithErrors, models.Provinces.options)),
        city => {
          models.Cities.insert(city)
          //        Home.flashing("success" -> "City %s has been created".format(city.name))
          Redirect(routes.Cities.view(city.id))
        }
      )
  }

  def list = Action {
    implicit request =>
      Ok(views.html.cities.list("Ajax"))
  }

  def json = Action(parse.urlFormEncoded) {
    implicit request =>
      request.body.keys.map(println(_))
      println("iDisplayStart: " + request.body.get("iDisplayStart").head)
      println("iDisplayLength: " + request.body.get("iDisplayLength").head)
      println("sEcho: " + request.body.get("sEcho").head)
      println("sSortDir_0: " + request.body.get("sSortDir_0").head)
      println("sColumns: " + request.body.get("sColumns").head)
      println("iSortCol_0: " + request.body.get("iSortCol_0").head)

      val page: Int = request.body.get("iDisplayStart").head.head.toInt + 1
      val pagesize = request.body.get("iDisplayLength").head.head.toInt
      val columns = request.body.get("sColumns").head
      val sortField = request.body.get("iSortCol_0").head.head.toInt
      val sortOrder = request.body.get("sSortDir_0").head.head
      val orderField = sortOrder match {
        case "asc" => sortField + 1
        case "desc" => -(sortField + 1)
      }
      val sEcho = request.body.get("sEcho").head.head

      try {
        val cities = timer.time(models.Cities.json(page, pagesize, orderField))

        println(cities)
        println(Json.toJson(cities))

        val json = Json.obj(
          "sEcho" -> sEcho,
          "iDisplayStart" -> page,
          "iTotalRecords" -> pagesize,
          "iTotalDisplayRecords" -> citiesCount,
          "aaData" -> cities
        )
        println(json)
        Ok(json)
      }
      catch {
        case e: IllegalArgumentException => {
          println("ERROR! " + e.getMessage())
          BadRequest(e.getMessage())
        }
      }
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    models.Cities.delete(id)
    Home.flashing("success" -> "City has been deleted")
  }

}