package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.TypCompetitions

case class TypCompetition(id: Option[Long],
                          code: String,
                          name: String,
                          isChampionship: Boolean,
                          typMatchId: Long)


object TypCompetitions extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[TypCompetition] =  DB.withSession {
    implicit session =>
      (for (c <- typCompetitions.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      typCompetitions.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[(TypCompetition, TypMatch)] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = for {c <- typCompetitions
          .sortBy(typCompetition => orderField match {
          case 1 => typCompetition.code.asc
          case -1 => typCompetition.code.desc
          case 2 => typCompetition.name.asc
          case -2 => typCompetition.name.desc
        })
          .drop(offset)
          .take(pageSize)
                     m <- c.typMatch
        } yield (c, m)

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[TypCompetition] =  DB.withSession {
    implicit session =>
      typCompetitions.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[TypCompetition] =  DB.withSession {
    implicit session =>
      typCompetitions.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[TypCompetition] =  DB.withSession {
    implicit session =>
      typCompetitions.where(_.code === code).firstOption
  }

  def insert(typCompetition: TypCompetition): Long =  DB.withSession {
    implicit session =>
      typCompetitions.insert(typCompetition)
  }

  def update(id: Long, typCompetition: TypCompetition) =  DB.withSession {
    implicit session =>
      val typCompetition2update = typCompetition.copy(Some(id), typCompetition.code, typCompetition.name)
      typCompetitions.where(_.id === id).update(typCompetition2update)
  }

  def delete(typCompetitionId: Long) =  DB.withSession {
    implicit session =>
      typCompetitions.where(_.id === typCompetitionId).delete
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
        item <- typCompetitions
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCompetitionFormat = Json.format[TypCompetition]

}

