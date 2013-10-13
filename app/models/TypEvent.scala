package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

import database.TypEvents

case class TypEvent(id: Option[Long],
                          code: String,
                          name: String)


object TypEvent{
  lazy val pageSize = 10

  def findAll: Seq[TypEvent] = DB.withSession {
    implicit session:Session => {
      (for (c <- TypEvents.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(TypEvents.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypEvent] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session:Session =>
        val typEvents = (
          for {c <- TypEvents
            .sortBy(typEvent => orderField match {
            case 1 => typEvent.code.asc
            case -1 => typEvent.code.desc
            case 2 => typEvent.name.asc
            case -2 => typEvent.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(typEvents, page, offset, count)
    }
  }

  def findById(id: Long): Option[TypEvent] = DB.withSession {
    implicit session:Session => {
      TypEvents.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[TypEvent] = DB.withSession {
    implicit session:Session => {
      TypEvents.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[TypEvent] = DB.withSession {
    implicit session:Session => {
      TypEvents.byCode(code).firstOption
    }
  }

  def insert(typEvent: TypEvent): Long = DB.withSession {
    implicit session:Session => {
      TypEvents.autoInc.insert(typEvent)
    }
  }

  def update(id: Long, typEvent: TypEvent) = DB.withSession {
    implicit session:Session => {
      val typEvent2update = typEvent.copy(Some(id), typEvent.code, typEvent.name)
      TypEvents.where(_.id === id).update(typEvent2update)
    }
  }

  def delete(typEventId: Long) = DB.withSession {
    implicit session:Session => {
      TypEvents.where(_.id === typEventId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- TypEvents
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typEventFormat = Json.format[TypEvent]

}

