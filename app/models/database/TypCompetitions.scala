package models.database

import play.api.db.slick.Config.driver.simple._
import models.TypCompetition

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:43
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object TypCompetitions extends Table[TypCompetition]("fam_typ_competition") {

  def id = column[Long]("id_typ_competition", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_competition")

  def code = column[String]("cod_typ_competition")

  def isChampionship = column[Boolean]("championship")

  def typMatchId = column[Long]("id_typ_match")

  def * = id.? ~ code ~ name ~ isChampionship ~ typMatchId <>(TypCompetition.apply _, TypCompetition.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)


}
