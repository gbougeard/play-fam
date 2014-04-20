package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class EventCategory(eventId: Long,
                     categoryId: Long)


object EventCategoryJson {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val ecJsonFormat = Json.format[EventCategory]

  import models.EventJson._
  import models.CategoryJson._

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

object EventCategories extends DAO{
  def findByEvent(id: Long): Seq[(EventCategory, Event, Category)] =  DB.withSession {
    implicit session =>
      val query = for {et <- eventCategories
                       if et.eventId === id
                       t <- et.category
                       e <- et.event

      } yield (et, e, t)
      query.list
  }

  def findByCategory(id: Long): Seq[(EventCategory, Event, Category)] =  DB.withSession {
    implicit session =>
      val query = for {et <- eventCategories
                       if et.categoryId === id
                       t <- et.category
                       e <- et.event

      } yield (et, e, t)
      query.list
  }

  def insert(event: EventCategory): Long =  DB.withSession {
    implicit session =>
      eventCategories.insert(event)
  }


//  def delete(eId: Long, tId : Long) =  {
//    implicit session:Session => {
//      eventCategories.where(_.eventId === eId).and( _.categoryId === tId).delete
//    }
//  }

}

