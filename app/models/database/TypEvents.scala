package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, TypEvent}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:46
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class TypEvents(tag:Tag) extends Table[TypEvent](tag, "fam_typ_event") {

  def id = column[Long]("id_typ_event", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_event")

  def code = column[String]("cod_typ_event")

  def * = (id.? , code , name )<>(TypEvent.tupled, TypEvent.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)



}
