package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Event._
import models.Category._
import database.EventCategories

case class EventCategory(eventId: Long,
                     categoryId: Long)

object EventCategory{
  def findByEvent(id: Long): Seq[(EventCategory, Event, Category)] = DB.withSession {
    implicit session:Session => {
      val query = for {et <- EventCategories
                       if et.eventId === id
                       t <- et.category
                       e <- et.event

      } yield (et, e, t)
      query.list
    }
  }

  def findByCategory(id: Long): Seq[(EventCategory, Event, Category)] = DB.withSession {
    implicit session:Session => {
      val query = for {et <- EventCategories
                       if et.categoryId === id
                       t <- et.category
                       e <- et.event

      } yield (et, e, t)
      query.list
    }
  }

  def insert(event: EventCategory): Long = DB.withSession {
    implicit session:Session => {
      EventCategories.insert(event)
    }
  }


//  def delete(eId: Long, tId : Long) = DB.withSession {
//    implicit session:Session => {
//      EventCategories.where(_.eventId === eId).and( _.categoryId === tId).delete
//    }
//  }


  implicit val eventCategoryFormat = Json.format[EventCategory]

  implicit val eventCatCompleteReads: Reads[(EventCategory, Event, Category)] = (
    (__ \ 'eventcategory).read[EventCategory] ~
      (__ \ 'event).read[Event] ~
      (__ \ 'category).read[Category]
    ) tupled

  implicit val eventCatCompleteWrites: Writes[(EventCategory, Event, Category)] = (
    (__ \ 'eventcategory).write[EventCategory] ~
      (__ \ 'event).write[Event] ~
      (__ \ 'category).write[Category]
    ) tupled

}

