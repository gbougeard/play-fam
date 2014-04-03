package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.Groups

case class Group(id: Option[Long],
                 name: String)

object Group{
  lazy val pageSize = 10

  def findAll: Seq[Group] =  {
    implicit session:Session => {
      (for (c <- Groups.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(Groups.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Group] = {

    val offset = pageSize * page

     {
      implicit session:Session =>
        val typCards = (
          for {c <- Groups
            .sortBy(typCard => orderField match {
            case 1 => typCard.name.asc
            case -1 => typCard.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(typCards, page, offset, count)
    }
  }

  def findById(id: Long): Option[Group] =  {
    implicit session:Session => {
      Groups.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Group] =  {
    implicit session:Session => {
      Groups.byName(name).firstOption
    }
  }


  def insert(typCard: Group): Long =  {
    implicit session:Session => {
      Groups.autoInc.insert(typCard)
    }
  }

  def update(id: Long, typCard: Group) =  {
    implicit session:Session => {
      val typCard2update = typCard.copy(Some(id), typCard.name)
      Groups.where(_.id === id).update(typCard2update)
    }
  }

  def delete(typCardId: Long) =  {
    implicit session:Session => {
      Groups.where(_.id === typCardId).delete
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
        item <- Groups
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCardFormat = Json.format[Group]

}

