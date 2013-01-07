package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Country
import models.Countries._
import play.api.libs.json.Json
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer


object Countries extends Controller {
  val metric = Metrics.defaultRegistry().newTimer(classOf[Country], "ajax")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Countries.list)
  lazy val countriesCount = models.Countries.count

  /**
   * Describe the country form (used in both edit and create screens).
   */
  val countryForm = Form(
    mapping(
      "id" -> longNumber,
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "upper" -> nonEmptyText,
      "lower" -> nonEmptyText
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Country.apply)(Country.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  def list = Action {
    implicit request =>
      Ok(views.html.provinces.list("Ajax"))
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
        val states = timer.time(models.Countries.json(page, pagesize, orderField))

        println(states)
        println(Json.toJson(states))

        val json = Json.obj(
          "sEcho" -> sEcho,
          "iDisplayStart" -> page,
          "iTotalRecords" -> pagesize,
          "iTotalDisplayRecords" -> countriesCount,
          "aaData" -> states
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

  def view(id: Long) = Action {
    implicit request =>
      models.Countries.findById(id).map {
        country => Ok(views.html.countries.view("View Country", country))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Countries.findById(id).map {
        country => Ok(views.html.countries.edit("Edit Country", id, countryForm.fill(country)))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      countryForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.countries.edit("Edit Country - errors", id, formWithErrors)),
        country => {
          models.Countries.update(country)
          //        Home.flashing("success" -> "Country %s has been updated".format(country.name))
          Redirect(routes.Countries.view(country.id))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    Ok(views.html.countries.create("New Country", countryForm))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      countryForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.countries.create("New Country - errors", formWithErrors)),
        country => {
          models.Countries.insert(country)
          //        Home.flashing("success" -> "Country %s has been created".format(country.name))
          Redirect(routes.Countries.view(country.id))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    models.Countries.delete(id)
    Home.flashing("success" -> "Country has been deleted")
  }

}