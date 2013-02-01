package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.City
import play.api.libs.json.Json
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import slick.session.Session
import models.common.AppDB
import models.common.JsonImplicits._


object Cities extends Controller {
  val metric = Metrics.defaultRegistry().newTimer(classOf[City], "ajax")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Cities.list(0, 0))

  lazy val database = AppDB.database
  lazy val dal = AppDB.dal


  /**
   * Describe the city form (used in both edit and create screens).
   */
  val cityForm = Form(
    mapping(
      "id" -> optional(longNumber),
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
      database.withSession {
        implicit session: Session =>
        dal.Cities.findById(id).map {
          city => Ok(views.html.cities.view("View City", city))
        } getOrElse (NotFound)
      }
  }

  def edit(id: Long) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
        dal.Cities.findById(id).map {
          city => Ok(views.html.cities.edit("Edit City", id, cityForm.fill(city), dal.Provinces.options))
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
        cityForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.cities.edit("Edit City - errors", id, formWithErrors, dal.Provinces.options)),
          city => {
            dal.Cities.update(city)
            Redirect(routes.Cities.edit(id)).flashing("success" -> "City %s has been updated".format(city.name))
            //Redirect(routes.Cities.view(city.id.get))
          }
        )
      }
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    database.withSession {
      implicit session: Session =>
      Ok(views.html.cities.create("New City", cityForm, dal.Provinces.options))
    }
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
        cityForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.cities.create("New City - errors", formWithErrors, dal.Provinces.options)),
          city => {
            dal.Cities.insert(city)
            //        Home.flashing("success" -> "City %s has been created".format(city.name))
            //Redirect(routes.Cities.view(city.id))
            Redirect(routes.Cities.create).flashing("success" -> "City %s has been created".format(city.name))

          }
        )
      }
  }

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      database.withSession {
        implicit session: Session =>
          val cities = dal.Cities.findPage(page, orderBy)
          val html = views.html.cities.list("Liste des cities", cities, orderBy)
          Ok(html)
      }
  }

  def json = Action(parse.urlFormEncoded) {
    implicit request =>
      database.withSession {
        implicit session: Session =>
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
          val cities = timer.time(dal.Cities.json(page, pagesize, orderField))

          println(cities)
          println(Json.toJson(cities))

          val json = Json.obj(
            "sEcho" -> sEcho,
            "iDisplayStart" -> page,
            "iTotalRecords" -> pagesize,
            "iTotalDisplayRecords" -> dal.Cities.count,
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
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    database.withSession {
      implicit session: Session =>
      dal.Cities.delete(id)
      Home.flashing("success" -> "City has been deleted")
    }
  }

}