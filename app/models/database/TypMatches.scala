package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, TypMatch}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:51
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class TypMatches(tag:Tag) extends Table[TypMatch](tag, "fam_typ_match") {

  def id = column[Long]("id_typ_match", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_match")

  def code = column[String]("cod_typ_match")

  def nbSubstitute = column[Int]("nb_substitute")

  def nbPlayer = column[Int]("nb_player")

  def periodDuration = column[Int]("period_duration")

  def hasExtraTime = column[Boolean]("extra_time")

  def extraTimeDuration = column[Int]("extra_time_duration")

  def hasInfiniteSubs = column[Boolean]("infinite_subs")

  def nbSubstitution = column[Int]("nb_substitution")

  def hasPenalties = column[Boolean]("penalties")

  def nbPenalties = column[Int]("nb_penalties")

  def * = (id.? , code , name , nbSubstitute , nbPlayer , periodDuration , hasExtraTime , extraTimeDuration.? , hasInfiniteSubs , nbSubstitution.? , hasPenalties , nbPenalties.? )



}
