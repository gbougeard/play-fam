package models.database

import play.api.db.slick.Config.driver.simple._
import models.SeasonCompetition

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object SeasonCompetitions extends Table[SeasonCompetition]("fam_season_competition") {

  def id = column[Long]("id_season_competition", O.PrimaryKey, O.AutoInc)

  def categoryId = column[Long]("id_category")

  def scaleId = column[Long]("id_scale")

  def seasonId = column[Long]("id_season")

  def typCompetitionId = column[Long]("id_typ_competition")

  def * = id.? ~ categoryId ~ scaleId ~ seasonId ~ typCompetitionId <>(SeasonCompetition.apply _, SeasonCompetition.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def category = foreignKey("CATEGORY_FK", categoryId, Categories)(_.id)

  def scale = foreignKey("SCALE_FK", scaleId, Scales)(_.id)

  def season = foreignKey("SEASON_FK", seasonId, Seasons)(_.id)

  def typCompetition = foreignKey("TYP_COMPETITION_FK", typCompetitionId, TypCompetitions)(_.id)


  val byId = createFinderBy(_.id)
  val bySeason = createFinderBy(_.seasonId)
  val byTypCompetition = createFinderBy(_.typCompetitionId)
  val byCategory = createFinderBy(_.categoryId)


}
