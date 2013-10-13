package models.database

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime

import com.github.tototoshi.slick.JodaSupport._

import models.Event

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Events extends Table[Event]("fam_event") {

  def id = column[Long]("id_event", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_event")

  def duration = column[Int]("duration")

  def dtEvent = column[DateTime]("dt_event")

  def typEventId = column[Long]("id_typ_event")

  def placeId = column[Long]("id_place")

  def eventStatusId = column[Long]("id_eventStatus")

  def comments = column[String]("comments")

  def * = id.? ~ dtEvent ~ duration ~ name ~ typEventId ~ placeId.? ~ eventStatusId ~ comments.? <>(Event.apply _ , Event.unapply _)

  def autoInc =  * returning id

  // A reified foreign key relation that can be navigated to create a join
  def typEvent = foreignKey("TYP_EVENT_FK", typEventId, TypEvents)(_.id)

  def place = foreignKey("PLACE_FK", placeId, Places)(_.id)

  def eventStatus = foreignKey("EVENT_STATUS_FK", eventStatusId, EventStatuses)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)


}
