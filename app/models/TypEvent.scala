package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


case class TypEvent(id: Option[Long],
                          code: String,
                          name: String)


object TypEventJson {
  import play.api.libs.json._
  implicit val teJsonFormat = Json.format[TypEvent]
}


object TypEvents extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[TypEvent] =  DB.withSession {
    implicit session =>
      (for (c <- typEvents.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      typEvents.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypEvent] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q= for {c <- typEvents
          .sortBy(typEvent => orderField match {
          case 1 => typEvent.code.asc
          case -1 => typEvent.code.desc
          case 2 => typEvent.name.asc
          case -2 => typEvent.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[TypEvent] =  DB.withSession {
    implicit session =>
      typEvents.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[TypEvent] =  DB.withSession {
    implicit session =>
      typEvents.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[TypEvent] =  DB.withSession {
    implicit session =>
      typEvents.where(_.code === code).firstOption
  }

  def insert(typEvent: TypEvent): Long =  DB.withSession {
    implicit session =>
      typEvents.insert(typEvent)
  }

  def update(id: Long, typEvent: TypEvent) =  DB.withSession {
    implicit session =>
      val typEvent2update = typEvent.copy(Some(id), typEvent.code, typEvent.name)
      typEvents.where(_.id === id).update(typEvent2update)
  }

  def delete(typEventId: Long) =  DB.withSession {
    implicit session =>
      typEvents.where(_.id === typEventId).delete
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
        item <- typEvents
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}

