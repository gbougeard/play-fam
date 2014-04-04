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

  def findAll(implicit session: Session): Seq[TypCard] =  {
      (for (c <- typCards.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      (typCards.length).run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[TypCard] = {

    val offset = pageSize * page

        val typCards = (
          for {c <- typCards
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

  def findById(id: Long)(implicit session: Session): Option[TypCard] =  {
      typCards.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[TypCard] =  {
      typCards.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[TypCard] =  {
      typCards.where(_.code === code).firstOption
  }

  def insert(typCard: TypCard)(implicit session: Session): Long =  {
      typCards.insert(typCard)
  }

  def update(id: Long, typCard: TypCard)(implicit session: Session) =  {
      val typCard2update = typCard.copy(Some(id), typCard.code, typCard.name)
      typCards.where(_.id === id).update(typCard2update)
  }

  def delete(typCardId: Long)(implicit session: Session) =  {
      typCards.where(_.id === typCardId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- typCards
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCardFormat = Json.format[TypCard]

}

