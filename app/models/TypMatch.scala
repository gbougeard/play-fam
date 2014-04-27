package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


case class TypMatch(id: Option[Long] = None,
                    code: String,
                    name: String,
                    nbSubstitute: Int,
                    nbPlayer: Int,
                    periodDuration: Int,
                    hasExtraTime: Boolean,
                    extraTimeDuration: Option[Int] = None,
                    hasInfiniteSubs: Boolean,
                    nbSubstitution: Option[Int] = None,
                    hasPenalties: Boolean,
                    nbPenalties: Option[Int] = None)

object TypMatchJson {
  import play.api.libs.json.Json
  implicit val typMatchJsonFormat = Json.format[TypMatch]
}


object TypMatches extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[TypMatch] =  DB.withSession {
    implicit session =>
      (for (c <- typMatches.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      typMatches.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypMatch] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = for {c <- typMatches
          .sortBy(typMatch => orderField match {
          case 1 => typMatch.code.asc
          case -1 => typMatch.code.desc
          case 2 => typMatch.name.asc
          case -2 => typMatch.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[TypMatch] =  DB.withSession {
    implicit session =>
      typMatches.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[TypMatch] =  DB.withSession {
    implicit session =>
      typMatches.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[TypMatch] =  DB.withSession {
    implicit session =>
      typMatches.where(_.code === code).firstOption
  }

  def insert(typMatch: TypMatch): Long =  DB.withSession {
    implicit session =>
      typMatches.insert(typMatch)
  }

  def update(id: Long, typMatch: TypMatch) =  DB.withSession {
    implicit session =>
      val typMatch2update = typMatch.copy(Some(id), typMatch.code, typMatch.name)
      typMatches.where(_.id === id).update(typMatch2update)
  }

  def delete(typMatchId: Long) =  DB.withSession {
    implicit session =>
      typMatches.where(_.id === typMatchId).delete
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
        item <- typMatches
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}

