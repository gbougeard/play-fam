package models.database

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.MappedTypeMapper
import java.util.Date
import models.Fixture

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Fixtures extends Table[Fixture]("fam_fixture") {

  implicit val javaUtilDateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Date](
    x => new java.sql.Date(x.getTime),
    x => new java.util.Date(x.getTime)
  )

  def id = column[Long]("id_fixture", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_fixture")

  def date = column[Date]("dt_fixture")

  def competitionId = column[Long]("id_season_competition")

  def * = id.? ~ date ~ name ~ competitionId <>(Fixture.apply _, Fixture.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def competition = foreignKey("COMPETTITION_FK", competitionId, SeasonCompetitions)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byDate = createFinderBy(_.date)


}
