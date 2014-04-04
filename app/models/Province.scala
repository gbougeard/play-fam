package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.State._
import database.Provinces

case class Province(id: Option[Long],
                    code: String,
                    name: String,
                    upper: String,
                    lower: String,
                    stateId: Long)

object Provinces extends DAO{
  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[Province] =  {
      (for (c <- provinces.sortBy(_.code)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      provinces.length.run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(Province, State)] = {

    val offset = pageSize * page

        val provinces = (
          for {p <- provinces

               s <- p.state
          } yield (p, s))
          .sortBy(orderField match {
          case 1 => _._1.code asc
          case -1 => _._1.code desc
          case 2 => _._1.name asc
          case -2 => _._1.name desc
          case 3 => _._2.name asc
          case -3 => _._2.name desc
        })
          .drop(offset)
          .take(pageSize)

        val totalRows = count
        Page(provinces.list, page, offset, totalRows)
  }

  def findById(id: Long)(implicit session: Session): Option[Province] =  {
      provinces.where(_.id === id).firstOption
    }

  def findByName(name: String)(implicit session: Session): Option[Province] =  {
      provinces.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[Province] =  {
      provinces.where(_.code === code).firstOption
  }

  def insert(province: Province)(implicit session: Session): Long =  {
      provinces.insert(province)
  }

  def update(id: Long, province: Province)(implicit session: Session) =  {
      provinces.where(_.id === province.id).update(province.copy(Some(id)))
  }

  def delete(provinceId: Long)(implicit session: Session) =  {
      provinces.where(_.id === provinceId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.code + " - " + c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- provinces
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  //JSON
  implicit val provinceFormat = Json.format[Province]

  implicit val provinceWithStateReads: Reads[(Province, State)] = (
    (__ \ 'province).read[Province] ~
      (__ \ 'state).read[State]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val provinceWithStateWrites: Writes[(Province, State)] = (
    (__ \ 'province).write[Province] ~
      (__ \ 'state).write[State]
    ) tupled
  implicit val provinceWithStateFormat = Format(provinceWithStateReads, provinceWithStateWrites)

}