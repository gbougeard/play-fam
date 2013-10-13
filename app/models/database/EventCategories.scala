package models.database

import play.api.db.slick.Config.driver.simple._
import models.EventCategory

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
 // define tables
private[models] object EventCategories extends Table[EventCategory]("fam_event_category") {

  def eventId = column[Long]("id_event")

  def categoryId = column[Long]("id_category")

  def * = eventId ~ categoryId <>(EventCategory.apply _, EventCategory.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def category = foreignKey("TEAM_FK", categoryId, Categories)(_.id)

  def event = foreignKey("EVENT_FK", eventId, Events)(_.id)


}
