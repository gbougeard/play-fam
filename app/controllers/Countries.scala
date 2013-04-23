package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Country
import play.api.libs.json.Json
import com.yammer.metrics.Metrics
import com.yammer.metrics.scala.Timer
import slick.session.Session


object Countries extends Controller {
  val metricList = Metrics.defaultRegistry().newTimer(classOf[Country], "list")
  val timerList = new Timer(metricList)
  val metricView = Metrics.defaultRegistry().newTimer(classOf[Country], "view")
  val timerView = new Timer(metricView)
  val metricDisplayList = Metrics.defaultRegistry().newTimer(classOf[Country], "displayList")
  val timerDisplayList = new Timer(metricDisplayList)

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Countries.list(0, 0))

  /**
   * Describe the country form (used in both edit and create screens).
   */
  val countryForm = Form(
    mapping(
      "id" -> optional(longNumber),
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

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      play.Logger.info(request.session.data.toString())
      request.session.get("user").map { user =>
        play.Logger.info("Hello " + user)

      val countries = timerList.time(models.Countries.findPage(page, orderBy))
      val html = views.html.countries.list("Liste des countries", countries, orderBy)
      timerDisplayList.time(Ok(html))
      }.getOrElse {
        Unauthorized("Oops, you are not connected")
      }
  }


  def view(id: Long) = Action {
    implicit request =>
      timerView.time(models.Countries.findById(id)).map {
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
          models.Countries.update(id, country)
          Redirect(routes.Countries.edit(id)).flashing("success" -> "Country %s has been updated".format(country.name))
          //Redirect(routes.Countries.view(country.id))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create = Action {
    implicit request =>
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
          Redirect(routes.Countries.create).flashing("success" -> "Country %s has been created".format(country.name))
          //Redirect(routes.Countries.view(country.id))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    implicit request =>
      models.Countries.delete(id)
      Home.flashing("success" -> "Country has been deleted")
  }

}