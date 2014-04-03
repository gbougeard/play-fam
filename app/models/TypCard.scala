package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

import database.TypCards

case class TypCard(id: Option[Long],
                          code: String,
                          name: String)


object TypCard{
  lazy val pageSize = 10

  def findAll: Seq[TypCard] =  {
    implicit session:Session => {
      (for (c <- TypCards.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(TypCards.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypCard] = {

    val offset = pageSize * page

     {
      implicit session:Session =>
        val typCards = (
          for {c <- TypCards
            .sortBy(typCard => orderField match {
            case 1 => typCard.code.asc
            case -1 => typCard.code.desc
            case 2 => typCard.name.asc
            case -2 => typCard.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(typCards, page, offset, count)
    }
  }

  def findById(id: Long): Option[TypCard] =  {
    implicit session:Session => {
      TypCards.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[TypCard] =  {
    implicit session:Session => {
      TypCards.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[TypCard] =  {
    implicit session:Session => {
      TypCards.byCode(code).firstOption
    }
  }

  def insert(typCard: TypCard): Long =  {
    implicit session:Session => {
      TypCards.autoInc.insert(typCard)
    }
  }

  def update(id: Long, typCard: TypCard) =  {
    implicit session:Session => {
      val typCard2update = typCard.copy(Some(id), typCard.code, typCard.name)
      TypCards.where(_.id === id).update(typCard2update)
    }
  }

  def delete(typCardId: Long) =  {
    implicit session:Session => {
      TypCards.where(_.id === typCardId).delete
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
        item <- TypCards
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCardFormat = Json.format[TypCard]

}

