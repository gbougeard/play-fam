package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._

object Application extends Controller {


  def home = Action {
    val html = views.html.index("Hello")
    Ok(html)
  }

  // -- Javascript routing
  def javascriptRoutes = Action {
    implicit request =>
      import routes.javascript._
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          Places.gmapData
        )
      ).as("text/javascript")
  }

}