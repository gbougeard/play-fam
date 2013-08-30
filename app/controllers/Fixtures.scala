package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Fixture
import service.Coach


object Fixtures extends Controller with securesocial.core.SecureSocial{

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Fixtures.list(0, 0))

  /**
   * Describe the fixture form (used in both edit and create screens).
   */
  val fixtureForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "date" -> date("yyyy-MM-dd"),
      "name" -> nonEmptyText,
      "competitionId" -> longNumber
    )
      (Fixture.apply)(Fixture.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val fixtures = models.Fixtures.findPage(page, orderBy)
      val html = views.html.fixtures.list("Liste des fixtures", fixtures, orderBy)
      Ok(html)
  }

  def view(id: Long) = Action {
    implicit request =>
      models.Fixtures.findById(id).map {
        fixture => Ok(views.html.fixtures.view("View Fixture", fixture))
      } getOrElse (NotFound)
  }

  def edit(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      models.Fixtures.findById(id).map {
        fixture => Ok(views.html.fixtures.edit("Edit Fixture", id, fixtureForm.fill(fixture), models.SeasonCompetitions.options))
      } getOrElse (NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
  def update(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      fixtureForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.fixtures.edit("Edit Fixture - errors", id, formWithErrors, models.SeasonCompetitions.options)),
        fixture => {
          models.Fixtures.update(id, fixture)
          //        Home.flashing("success" -> "Fixture %s has been updated".format(fixture.name))
          Redirect(routes.Fixtures.list(0, 2))
        }
      )
  }

  /**
   * Display the 'new computer form'.
   */
  def create =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      Ok(views.html.fixtures.create("New Fixture", fixtureForm, models.Clubs.options))
  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      fixtureForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.fixtures.create("New Fixture - errors", formWithErrors, models.SeasonCompetitions.options)),
        fixture => {
          models.Fixtures.insert(fixture)
          //        Home.flashing("success" -> "Fixture %s has been created".format(fixture.name))
          Redirect(routes.Fixtures.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      models.Fixtures.delete(id)
      Home.flashing("success" -> "Fixture has been deleted")
  }

}