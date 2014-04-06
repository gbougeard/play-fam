package models.database

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime

import com.github.tototoshi.slick.MySQLJodaSupport._

import models.Event
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Events(tag:Tag) extends Table[Event](tag, "fam_event") {

  def id = column[Long]("id_event", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_event")

  def duration = column[Int]("duration")

  def dtEvent = column[DateTime]("dt_event")

  def typEventId = column[Long]("id_typ_event")

  def placeId = column[Long]("id_place")

  def eventStatusId = column[Long]("id_eventStatus")

  def comments = column[String]("comments")

  def * = (id.? , dtEvent , duration , name , typEventId , placeId.? , eventStatusId , comments.? )<>(Event.tupled, Event.unapply _)


  // A reified foreign key relation that can be navigated to create a join
  def typEvent = foreignKey("TYP_EVENT_FK", typEventId, TableQuery[TypEvents])(_.id)

  def place = foreignKey("PLACE_FK", placeId, TableQuery[Places])(_.id)

  def eventStatus = foreignKey("EVENT_STATUS_FK", eventStatusId, TableQuery[EventStatuses])(_.id)


}
