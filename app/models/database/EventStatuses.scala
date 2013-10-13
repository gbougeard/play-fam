package models.database

import play.api.db.slick.Config.driver.simple._
import models.EventStatus

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */

// define tables
private[models] object EventStatuses extends Table[EventStatus]("fam_event_status") {

  def id = column[Long]("id_event_status", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_event_status")

  def code = column[String]("cod_event_status")

  def * = id.? ~ code ~ name <>(EventStatus.apply _, EventStatus.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)


}
