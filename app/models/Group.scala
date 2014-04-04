package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.Groups

case class Group(id: Option[Long],
                 name: String)

object Groups extends DAO{
  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[Group] =  {
      (for (c <- groups.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      groups.length.run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Group] = {

    val offset = pageSize * page

        val q = for {c <- groups
          .sortBy(typCard => orderField match {
          case 1 => typCard.name.asc
          case -1 => typCard.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c

        Page(q.list, page, offset, count)
  }

  def findById(id: Long)(implicit session: Session): Option[Group] =  {
      groups.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[Group] =  {
      groups.where(_.name === name).firstOption
  }


  def insert(typCard: Group)(implicit session: Session): Long =  {
      groups.insert(typCard)
  }

  def update(id: Long, typCard: Group)(implicit session: Session) =  {
      val typCard2update = typCard.copy(Some(id), typCard.name)
      groups.where(_.id === id).update(typCard2update)
  }

  def delete(typCardId: Long)(implicit session: Session) =  {
      groups.where(_.id === typCardId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {
  //    c <- findAll
  //  } yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- groups
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCardFormat = Json.format[Group]

}

