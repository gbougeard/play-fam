package models.database

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import models.Fixture
import org.joda.time.DateTime
import com.github.tototoshi.slick.MySQLJodaSupport._
/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Fixtures(tag:Tag) extends Table[Fixture](tag, "fam_fixture") {

  def id = column[Long]("id_fixture", O.PrimaryKey, O.AutoInc)

  def date = column[DateTime]("dt_fixture")

  def name = column[String]("lib_fixture")

  def competitionId = column[Long]("id_season_competition")

  def * = (id.? , date , name , competitionId )<>(Fixture.tupled, Fixture.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def competition = foreignKey("COMPETTITION_FK", competitionId, TableQuery[SeasonCompetitions])(_.id)

}
