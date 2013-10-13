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


object SeasonCompetition{
  lazy val pageSize = 10

  def findAll: Seq[SeasonCompetition] = DB.withSession {
    implicit session:Session => {
      (for (c <- SeasonCompetitions.sortBy(_.typCompetitionId)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(SeasonCompetitions.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(SeasonCompetition, Category, Scale, Season, TypCompetition)] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session:Session =>
        val seasonCompetitions = (
          for {sc <- SeasonCompetitions
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

        Page(seasonCompetitions.list, page, offset, count)
    }
  }

  def findById(id: Long): Option[SeasonCompetition] = DB.withSession {
    implicit session:Session => {
      SeasonCompetitions.byId(id).firstOption
    }
  }

  def findByIdComplete(id: Long): Option[(SeasonCompetition, Season, TypCompetition)] = DB.withSession {
    implicit session:Session => {
      val query = (
        for {sc <- SeasonCompetitions
             if sc.id === id
             s <- sc.season
             c <- sc.typCompetition
        } yield (sc, s, c))
      query.firstOption
    }
  }

  def findByCategory(category: Long): Option[SeasonCompetition] = DB.withSession {
    implicit session:Session => {
      SeasonCompetitions.byCategory(category).firstOption
    }
  }

  def findBySeason(season: Long): Option[SeasonCompetition] = DB.withSession {
    implicit session:Session => {
      SeasonCompetitions.bySeason(season).firstOption
    }
  }

  def findByTypCompetition(typCompetition: Long): Option[SeasonCompetition] = DB.withSession {
    implicit session:Session => {
      SeasonCompetitions.byTypCompetition(typCompetition).firstOption
    }
  }

  def insert(seasonCompetition: SeasonCompetition): Long = DB.withSession {
    implicit session:Session => {
      SeasonCompetitions.autoInc.insert((seasonCompetition))
    }
  }

  def update(id: Long, seasonCompetition: SeasonCompetition) = DB.withSession {
    implicit session:Session => {
      val seasonCompetition2update = seasonCompetition.copy(Some(id))
      SeasonCompetitions.where(_.id === id).update(seasonCompetition2update)
    }
  }

  def delete(seasonCompetitionId: Long) = DB.withSession {
    implicit session:Session => {
      SeasonCompetitions.where(_.id === seasonCompetitionId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {
    c <- findAll
  } yield (c.id.toString, c.typCompetitionId.toString())

  def optionsChampionship: Seq[(Long, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- SeasonCompetitions
        s <- item.season
        tc <- item.typCompetition
        if tc.isChampionship
      } yield (item.id, s.name, tc.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1, row._2 + " "+ row._3))
  }

  implicit val seasonCompetitionFormat = Json.format[SeasonCompetition]

}

