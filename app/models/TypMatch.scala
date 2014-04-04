package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.TypMatches

case class TypMatch(id: Option[Long],
                    code: String,
                    name: String,
                    nbSubstitute: Int,
                    nbPlayer: Int,
                    periodDuration: Int,
                    hasExtraTime: Boolean,
                    extraTimeDuration: Option[Int],
                    hasInfiniteSubs: Boolean,
                    nbSubstitution: Option[Int],
                    hasPenalties: Boolean,
                    nbPenalties: Option[Int])


object TypMatches extends DAO{
  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[TypMatch] =  {
      (for (c <- typMatches.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      (typMatches.length).run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[TypMatch] = {

    val offset = pageSize * page

        val q = (
          for {c <- typMatches
            .sortBy(typMatch => orderField match {
            case 1 => typMatch.code.asc
            case -1 => typMatch.code.desc
            case 2 => typMatch.name.asc
            case -2 => typMatch.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c)

        Page(q.list, page, offset, count)
  }

  def findById(id: Long)(implicit session: Session): Option[TypMatch] =  {
      typMatches.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[TypMatch] =  {
      typMatches.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[TypMatch] =  {
      typMatches.where(_.code === code).firstOption
  }

  def insert(typMatch: TypMatch)(implicit session: Session): Long =  {
      typMatches.insert(typMatch)
  }

  def update(id: Long, typMatch: TypMatch)(implicit session: Session) =  {
      val typMatch2update = typMatch.copy(Some(id), typMatch.code, typMatch.name)
      typMatches.where(_.id === id).update(typMatch2update)
  }

  def delete(typMatchId: Long)(implicit session: Session) =  {
      typMatches.where(_.id === typMatchId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- typMatches
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typMatchFormat = Json.format[TypMatch]

}

