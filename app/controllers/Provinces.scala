package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Province
import models.Provinces._
import play.api.libs.json.Json
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer


object Provinces extends Controller {
  val metric = Metrics.defaultRegistry().newTimer(classOf[Province], "ajax")
  val timer = new Timer(metric)
  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Provinces.list)
  lazy val provincesCount = models.Provinces.count

  /**
   * Describe the province form (used in both edit and create screens).
   */
  val provinceForm = Form(
    mapping(
      "id" -> longNumber,
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "upper" -> nonEmptyText,
      "lower" -> nonEmptyText,
      "stateId" -> longNumber
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (Province.apply)(Province.unapply)
  )

  // -- Actions
  /**
   * Handle default path requests, redirect to computers list
   */
  def index = Action {
    Home
  }

  //  def list = Action {
  //    val provinces = models.Provinces.findAll
  //    val html = views.html.provinces("Liste des provinces", provinces)
  //    Ok(html)
  //  }

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
        val provinces = timer.time(models.Provinces.json(page, pagesize, orderField))

        println(provinces)
        println(Json.toJson(provinces))

        val json = Json.obj(
          "sEcho" -> sEcho,
          "iDisplayStart" -> page,
          "iTotalRecords" -> pagesize,
          "iTotalDisplayRecords" -> provincesCount,
          "aaData" -> provinces
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
      models.Provinces.findById(id).map {
        province => Ok(views.html.provinces.view("View Province", province))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.Provinces.findById(id).map {
        province => Ok(views.html.provinces.edit("Edit Province", id, provinceForm.fill(province), models.States.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      provinceForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.provinces.edit("Edit Province - errors", id, formWithErrors, models.States.options)),
        province => {
          models.Provinces.update(province)
          //        Home.flashing("success" -> "Province %s has been updated".format(province.name))
          Redirect(routes.Provinces.view(province.id))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    Ok(views.html.provinces.create("New Province", provinceForm, models.States.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      provinceForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.provinces.create("New Province - errors", formWithErrors, models.States.options)),
        province => {
          models.Provinces.insert(province)
          //        Home.flashing("success" -> "Province %s has been created".format(province.name))
          Redirect(routes.Provinces.view(province.id))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    models.Provinces.delete(id)
    Home.flashing("success" -> "Province has been deleted")
  }

}