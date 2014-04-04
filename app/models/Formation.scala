package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.Formations

case class Formation(id: Option[Long],
                     code: String,
                     name: String,
                     isDefault: Boolean,
                     typMatchId: Long)


object Formations extends DAO{
  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[Formation] =  {
      (for (c <- formations.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      formations.length.run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(Formation, TypMatch)] = {

    val offset = pageSize * page

        val q = for {c <- formations
          .sortBy(formation => orderField match {
          case 1 => formation.code.asc
          case -1 => formation.code.desc
          case 2 => formation.name.asc
          case -2 => formation.name.desc
        })

          .drop(offset)
          .take(pageSize)
                     tm <- c.typMatch
        } yield (c, tm)

        Page(q.list, page, offset, count)
  }

  def findById(id: Long)(implicit session: Session): Option[Formation] =  {
      formations.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[Formation] =  {
      formations.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[Formation] =  {
      formations.where(_.code === code).firstOption
  }

  def insert(formation: Formation)(implicit session: Session): Long =  {
      formations.insert(formation)
  }

  def update(id: Long, formation: Formation)(implicit session: Session) =  {
      val formation2update = formation.copy(Some(id), formation.code, formation.name)
      formations.where(_.id === id).update(formation2update)
  }

  def delete(formationId: Long)(implicit session: Session) =  {
      formations.where(_.id === formationId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {
  //    c <- findAll
  //  } yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- formations
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val formationFormat = Json.format[Formation]

}

