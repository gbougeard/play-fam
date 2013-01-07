package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.State
import models.States._
import play.api.libs.json.Json
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer



object States extends Controller {
  val metric = Metrics.defaultRegistry().newTimer(classOf[State], "ajax")
  val timer = new Timer(metric)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.States.list)
  lazy val statesCount = models.States.count

  /**
   * Describe the state form (used in both edit and create screens).
   */
  val stateForm = Form(
    mapping(
      "id" -> longNumber,
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "upper" -> nonEmptyText,
      "lower" -> nonEmptyText,
      "countryId" -> longNumber
      //      "discontinued" -> optional(date("yyyy-MM-dd")),
      //      "company" -> optional(longNumber)
    )
      (State.apply)(State.unapply)
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
      Ok(views.html.states.list("Ajax"))
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
        val states = timer.time(models.States.json(page, pagesize, orderField))

        println(states)
        println(Json.toJson(states))

        val json = Json.obj(
          "sEcho" -> sEcho,
          "iDisplayStart" -> page,
          "iTotalRecords" -> pagesize,
          "iTotalDisplayRecords" -> statesCount,
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
      models.States.findById(id).map {
        state => Ok(views.html.states.view("View State", state))
      } getOrElse (NotFound)
  }

  def edit(id: Long) = Action {
    implicit request =>
      models.States.findById(id).map {
        state => Ok(views.html.states.edit("Edit State", id, stateForm.fill(state), models.Countries.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) = Action {
    implicit request =>
      stateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.states.edit("Edit State - errors", id, formWithErrors, models.Countries.options)),
        state => {
          models.States.update(state)
          //        Home.flashing("success" -> "State %s has been updated".format(state.name))
          Redirect(routes.States.view(state.id))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    Ok(views.html.states.create("New State", stateForm, models.Countries.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save = Action {
    implicit request =>
      stateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.states.create("New State - errors", formWithErrors, models.Countries.options)),
        state => {
          models.States.insert(state)
          //        Home.flashing("success" -> "State %s has been created".format(state.name))
          Redirect(routes.States.view(state.id))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    models.States.delete(id)
    Home.flashing("success" -> "State has been deleted")
  }

}