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


object TypCompetition{
  lazy val pageSize = 10

  def findAll: Seq[TypCompetition] =  {
    implicit session:Session => {
      (for (c <- TypCompetitions.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(TypCompetitions.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(TypCompetition, TypMatch)] = {

    val offset = pageSize * page

     {
      implicit session:Session =>
        val typCompetitions = (
          for {c <- TypCompetitions
            .sortBy(typCompetition => orderField match {
            case 1 => typCompetition.code.asc
            case -1 => typCompetition.code.desc
            case 2 => typCompetition.name.asc
            case -2 => typCompetition.name.desc
          })
            .drop(offset)
            .take(pageSize)
               m <- c.typMatch
          } yield (c,m)).list

        Page(typCompetitions, page, offset, count)
    }
  }

  def findById(id: Long): Option[TypCompetition] =  {
    implicit session:Session => {
      TypCompetitions.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[TypCompetition] =  {
    implicit session:Session => {
      TypCompetitions.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[TypCompetition] =  {
    implicit session:Session => {
      TypCompetitions.byCode(code).firstOption
    }
  }

  def insert(typCompetition: TypCompetition): Long =  {
    implicit session:Session => {
      TypCompetitions.autoInc.insert(typCompetition)
    }
  }

  def update(id: Long, typCompetition: TypCompetition) =  {
    implicit session:Session => {
      val typCompetition2update = typCompetition.copy(Some(id), typCompetition.code, typCompetition.name)
      TypCompetitions.where(_.id === id).update(typCompetition2update)
    }
  }

  def delete(typCompetitionId: Long) =  {
    implicit session:Session => {
      TypCompetitions.where(_.id === typCompetitionId).delete
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
        item <- TypCompetitions
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCompetitionFormat = Json.format[TypCompetition]

}

