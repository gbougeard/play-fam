package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Events._
import models.Categorys._

case class EventCategory(eventId: Long,
                     categoryId: Long)

// define tables
object EventCategorys extends Table[EventCategory]("fam_event_category") {

  def eventId = column[Long]("id_event")

  def categoryId = column[Long]("id_category")

  def * = eventId ~ categoryId <>(EventCategory, EventCategory.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def category = foreignKey("TEAM_FK", categoryId, Categorys)(_.id)

  def event = foreignKey("EVENT_FK", eventId, Events)(_.id)

  def findByEvent(id: Long): Seq[(EventCategory, Event, Category)] = DB.withSession {
    implicit session => {
      val query = (
        for {et <- EventCategorys
             if et.eventId === id
             t <- et.category
             e <- et.event

        } yield (et, e, t))
      query.list
    }
  }

  def findByCategory(id: Long): Seq[(EventCategory, Event, Category)] = DB.withSession {
    implicit session => {
      val query = (
        for {et <- EventCategorys
             if et.categoryId === id
             t <- et.category
             e <- et.event

        } yield (et, e, t))
      query.list
    }
  }

  def insert(event: EventCategory): Long = DB.withSession {
    implicit session => {
      EventCategorys.insert((event))
    }
  }


//  def delete(eId: Long, tId : Long) = DB.withSession {
//    implicit session => {
//      EventCategorys.where(_.eventId === eId).and( _.categoryId === tId).delete
//    }
//  }


  implicit val eventFormat = Json.format[EventCategory]

  implicit val eventCompleteReads: Reads[(EventCategory, Event, Category)] = (
    (__ \ 'eventcategory).read[EventCategory] ~
      (__ \ 'event).read[Event] ~
      (__ \ 'category).read[Category]
    ) tupled

  implicit val eventCompleteWrites: Writes[(EventCategory, Event, Category)] = (
    (__ \ 'eventcategory).write[EventCategory] ~
      (__ \ 'event).write[Event] ~
      (__ \ 'category).write[Category]
    ) tupled

}

