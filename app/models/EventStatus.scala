package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

import database.EventStatuses

case class EventStatus(id: Option[Long],
                          code: String,
                          name: String)

object EventStatuses extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[EventStatus] =  DB.withSession {
    implicit session =>
      (for (c <- eventStatuses.sortBy(_.name)) yield c).list
  }

  def findPage(page: Int = 0, orderField: Int): Page[EventStatus] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = (
          for {c <- eventStatuses
            .sortBy(eventStatus => orderField match {
            case 1 => eventStatus.code.asc
            case -1 => eventStatus.code.desc
            case 2 => eventStatus.name.asc
            case -2 => eventStatus.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        val totalRows = eventStatuses.length.run
        Page(q, page, offset, totalRows)
  }

  def findById(id: Long): Option[EventStatus] =  DB.withSession {
    implicit session =>
      eventStatuses.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[EventStatus] =  DB.withSession {
    implicit session =>
      eventStatuses.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[EventStatus] =  DB.withSession {
    implicit session =>
      eventStatuses.where(_.code === code).firstOption
  }

  def insert(eventStatus: EventStatus): Long =  DB.withSession {
    implicit session =>
      eventStatuses.insert(eventStatus)
  }

  def update(id: Long, eventStatus: EventStatus) =  DB.withSession {
    implicit session =>
      val eventStatus2update = eventStatus.copy(Some(id), eventStatus.code, eventStatus.name)
      eventStatuses.where(_.id === id).update(eventStatus2update)
  }

  def delete(eventStatusId: Long) =  DB.withSession {
    implicit session =>
      eventStatuses.where(_.id === eventStatusId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- eventStatuses
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val eventStatusFormat = Json.format[EventStatus]

}

