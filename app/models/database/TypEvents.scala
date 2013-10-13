package models.database

import play.api.db.slick.Config.driver.simple._
import models.TypEvent

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:46
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object TypEvents extends Table[TypEvent]("fam_typ_event") {

  def id = column[Long]("id_typ_event", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_event")

  def code = column[String]("cod_typ_event")

  def * = id.? ~ code ~ name <>(TypEvent.apply _, TypEvent.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)



}
