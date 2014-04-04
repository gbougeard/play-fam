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

  def findAll(implicit session: Session): Seq[TypCompetition] =  {
      (for (c <- typCompetitions.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      (typCompetitions.length).run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(TypCompetition, TypMatch)] = {

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

  def findById(id: Long)(implicit session: Session): Option[TypCompetition] =  {
      typCompetitions.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[TypCompetition] =  {
      typCompetitions.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[TypCompetition] =  {
      typCompetitions.where(_.code === code).firstOption
  }

  def insert(typCompetition: TypCompetition)(implicit session: Session): Long =  {
      typCompetitions.insert(typCompetition)
  }

  def update(id: Long, typCompetition: TypCompetition)(implicit session: Session) =  {
      val typCompetition2update = typCompetition.copy(Some(id), typCompetition.code, typCompetition.name)
      typCompetitions.where(_.id === id).update(typCompetition2update)
  }

  def delete(typCompetitionId: Long)(implicit session: Session) =  {
      typCompetitions.where(_.id === typCompetitionId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
    implicit session:Session =>
      val query = (for {
        item <- typCompetitions
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCompetitionFormat = Json.format[TypCompetition]

}

