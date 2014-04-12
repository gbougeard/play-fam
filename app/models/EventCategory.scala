package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class EventCategory(eventId: Long,
                     categoryId: Long)

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

