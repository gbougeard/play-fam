package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, SeasonCompetition}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class SeasonCompetitions(tag:Tag) extends Table[SeasonCompetition](tag, "fam_season_competition") {

  def id = column[Long]("id_season_competition", O.PrimaryKey, O.AutoInc)

  def categoryId = column[Long]("id_category")

  def scaleId = column[Long]("id_scale")

  def seasonId = column[Long]("id_season")

  def typCompetitionId = column[Long]("id_typ_competition")

  def * = (id.? , categoryId , scaleId , seasonId , typCompetitionId )


  // A reified foreign key relation that can be navigated to create a join
  def category = foreignKey("CATEGORY_FK", categoryId, TableQuery[Categories])(_.id)

  def scale = foreignKey("SCALE_FK", scaleId, TableQuery[Scales])(_.id)

  def season = foreignKey("SEASON_FK", seasonId, TableQuery[Seasons])(_.id)

  def typCompetition = foreignKey("TYP_COMPETITION_FK", typCompetitionId, TableQuery[TypCompetitions])(_.id)

}
