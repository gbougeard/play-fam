package models.database

import play.api.db.slick.Config.driver.simple._
import models.TypCompetition
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:43
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class TypCompetitions(tag:Tag) extends Table[TypCompetition](tag, "fam_typ_competition") {

  def id = column[Long]("id_typ_competition", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_competition")

  def code = column[String]("cod_typ_competition")

  def isChampionship = column[Boolean]("championship")

  def typMatchId = column[Long]("id_typ_match")

  def * = (id.? , code , name , isChampionship , typMatchId)<>(TypCompetition.tupled, TypCompetition.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def typMatch = foreignKey("TC_TYP_MATCH_FK", typMatchId, TableQuery[TypMatches])(_.id)

}
