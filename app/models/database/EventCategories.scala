package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, EventCategory}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
 // define tables
  class EventCategories(tag:Tag) extends Table[EventCategory](tag, "fam_event_category") {

  def eventId = column[Long]("id_event")

  def categoryId = column[Long]("id_category")

  def * = (eventId , categoryId )<>(EventCategory.tupled, EventCategory.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def category = foreignKey("TEAM_FK", categoryId, TableQuery[Categories])(_.id)

  def event = foreignKey("EVENT_FK", eventId, TableQuery[Events])(_.id)


}
