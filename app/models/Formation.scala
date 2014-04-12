package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class Formation(id: Option[Long],
                     code: String,
                     name: String,
                     isDefault: Boolean,
                     typMatchId: Long)


object Formations extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[Formation] =  DB.withSession {
    implicit session =>
      (for (c <- formations.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      formations.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Formation, TypMatch)] = DB.withSession {
    implicit session =>

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

  def findById(id: Long): Option[Formation] =  DB.withSession {
    implicit session =>
      formations.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[Formation] =  DB.withSession {
    implicit session =>
      formations.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[Formation] =  DB.withSession {
    implicit session =>
      formations.where(_.code === code).firstOption
  }

  def insert(formation: Formation): Long =  DB.withSession {
    implicit session =>
      formations.insert(formation)
  }

  def update(id: Long, formation: Formation) =  DB.withSession {
    implicit session =>
      val formation2update = formation.copy(Some(id), formation.code, formation.name)
      formations.where(_.id === id).update(formation2update)
  }

  def delete(formationId: Long) =  DB.withSession {
    implicit session =>
      formations.where(_.id === formationId).delete
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
        item <- formations
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}

