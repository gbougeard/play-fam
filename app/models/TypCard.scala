package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

import database.TypCards

case class TypCard(id: Option[Long],
                          code: String,
                          name: String)


object TypCards extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[TypCard] =  DB.withSession {
    implicit session =>
      (for (c <- typCards.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      typCards.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypCard] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = for {c <- typCards
          .sortBy(typCard => orderField match {
          case 1 => typCard.code.asc
          case -1 => typCard.code.desc
          case 2 => typCard.name.asc
          case -2 => typCard.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[TypCard] =  DB.withSession {
    implicit session =>
      typCards.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[TypCard] =  DB.withSession {
    implicit session =>
      typCards.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[TypCard] =  DB.withSession {
    implicit session =>
      typCards.where(_.code === code).firstOption
  }

  def insert(typCard: TypCard): Long =  DB.withSession {
    implicit session =>
      typCards.insert(typCard)
  }

  def update(id: Long, typCard: TypCard) =  DB.withSession {
    implicit session =>
      val typCard2update = typCard.copy(Some(id), typCard.code, typCard.name)
      typCards.where(_.id === id).update(typCard2update)
  }

  def delete(typCardId: Long) =  DB.withSession {
    implicit session =>
      typCards.where(_.id === typCardId).delete
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
        item <- typCards
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCardFormat = Json.format[TypCard]

}

