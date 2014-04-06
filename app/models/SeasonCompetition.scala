package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.SeasonCompetitions

case class SeasonCompetition(id: Option[Long],
                             categoryId: Long,
                             scaleId: Long,
                             seasonId: Long,
                             typCompetitionId: Long)


object SeasonCompetitions extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[SeasonCompetition] =  DB.withSession {
    implicit session =>
      (for (c <- seasonCompetitions.sortBy(_.typCompetitionId)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      seasonCompetitions.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[(SeasonCompetition, Category, Scale, Season, TypCompetition)] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = (
          for {sc <- seasonCompetitions
               c <- sc.category
               s <- sc.scale
               se <- sc.season
               tc <- sc.typCompetition
          } yield (sc, c, s, se, tc)
          ).sortBy(orderField match {
          case 1 => _._4.name.asc
          case -1 => _._4.name.desc
          case 2 => _._5.name.asc
          case -2 => _._5.name.desc
          case 3 => _._2.name.asc
          case -3 => _._2.name.desc
        })
          .drop(offset)
          .take(pageSize)

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[SeasonCompetition] =  DB.withSession {
    implicit session =>
      seasonCompetitions.where(_.id === id).firstOption
  }

  def findByIdComplete(id: Long): Option[(SeasonCompetition, Season, TypCompetition)] =  DB.withSession {
    implicit session =>
      val query = for {sc <- seasonCompetitions
                       if sc.id === id
                       s <- sc.season
                       c <- sc.typCompetition
      } yield (sc, s, c)
      query.firstOption
  }

  def findByCategory(category: Long): Option[SeasonCompetition] =  DB.withSession {
    implicit session =>
      seasonCompetitions.where(_.categoryId === category).firstOption
  }

  def findBySeason(season: Long): Option[SeasonCompetition] =  DB.withSession {
    implicit session =>
      seasonCompetitions.where(_.seasonId === season).firstOption
  }

  def findByTypCompetition(typCompetition: Long): Option[SeasonCompetition] =  DB.withSession {
    implicit session =>
      seasonCompetitions.where(_.typCompetitionId === typCompetition).firstOption
  }

  def insert(seasonCompetition: SeasonCompetition): Long =  DB.withSession {
    implicit session =>
      seasonCompetitions.insert(seasonCompetition)
  }

  def update(id: Long, seasonCompetition: SeasonCompetition) =  DB.withSession {
    implicit session =>
      val seasonCompetition2update = seasonCompetition.copy(Some(id))
      seasonCompetitions.where(_.id === id).update(seasonCompetition2update)
  }

  def delete(seasonCompetitionId: Long) =  DB.withSession {
    implicit session =>
      seasonCompetitions.where(_.id === seasonCompetitionId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = DB.withSession {
    implicit session => for {
      c <- findAll
    } yield (c.id.toString, c.typCompetitionId.toString)
  }

  def optionsChampionship: Seq[(Long, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- seasonCompetitions
        s <- item.season
        tc <- item.typCompetition
        if tc.isChampionship
      } yield (item.id, s.name, tc.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1, row._2 + " "+ row._3))
  }

  implicit val seasonCompetitionFormat = Json.format[SeasonCompetition]

}

