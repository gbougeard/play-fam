package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

import database.EventStatuses

case class EventStatus(id: Option[Long],
                          code: String,
                          name: String)

object EventStatus{
  lazy val pageSize = 10

  def findAll: Seq[EventStatus] =  {
    implicit session:Session => {
      (for (c <- EventStatuses.sortBy(_.name)) yield c).list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[EventStatus] = {

    val offset = pageSize * page

     {
      implicit session:Session =>
        val eventStatuss = (
          for {c <- EventStatuses
            .sortBy(eventStatus => orderField match {
            case 1 => eventStatus.code.asc
            case -1 => eventStatus.code.desc
            case 2 => eventStatus.name.asc
            case -2 => eventStatus.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        val totalRows = (for (c <- EventStatuses) yield c.id).list.size
        Page(eventStatuss, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[EventStatus] =  {
    implicit session:Session => {
      EventStatuses.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[EventStatus] =  {
    implicit session:Session => {
      EventStatuses.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[EventStatus] =  {
    implicit session:Session => {
      EventStatuses.byCode(code).firstOption
    }
  }

  def insert(eventStatus: EventStatus): Long =  {
    implicit session:Session => {
      EventStatuses.autoInc.insert(eventStatus)
    }
  }

  def update(id: Long, eventStatus: EventStatus) =  {
    implicit session:Session => {
      val eventStatus2update = eventStatus.copy(Some(id), eventStatus.code, eventStatus.name)
      EventStatuses.where(_.id === id).update(eventStatus2update)
    }
  }

  def delete(eventStatusId: Long) =  {
    implicit session:Session => {
      EventStatuses.where(_.id === eventStatusId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  {
    implicit session:Session =>
      val query = (for {
        item <- EventStatuses
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val eventStatusFormat = Json.format[EventStatus]

}

