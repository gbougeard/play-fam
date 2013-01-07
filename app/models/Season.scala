package models

import common.Profile

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Season(id: Option[Long],
                current: Boolean,
                name: String)

trait SeasonComponent {
  this: Profile =>

  import profile.simple._

  implicit val seasonFormat = Json.format[Season]

  // define tables
  object Seasons extends Table[Season]("fam_season") {

    def id = column[Long]("id_season", O.PrimaryKey, O.AutoInc)

    def name = column[String]("lib_season")

    def current = column[Boolean]("current_season")

    def * = id.? ~ current ~ name <>(Season, Season.unapply _)

    def autoInc = id.? ~ current ~ name <>(Season, Season.unapply _) returning id

    val byId = createFinderBy(_.id)
    val byName = createFinderBy(_.name)
    val byCurrent = createFinderBy(_.current)

    lazy val pageSize = 10

    def findAll(implicit session: Session): Seq[Season] = {
      (for (c <- Seasons.sortBy(_.name)) yield c).list
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Season] = {

      val offset = pageSize * page
      val seasons = (
        for {c <- Seasons
          .sortBy(season => orderField match {
          case 1 => season.current.asc
          case -1 => season.current.desc
          case 2 => season.name.asc
          case -2 => season.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c).list

      val totalRows = (for (c <- Seasons) yield c.id).list.size
      Page(seasons, page, offset, totalRows)
    }

    def findById(id: Long)(implicit session: Session): Option[Season] = {
      Seasons.byId(id).firstOption
    }

    def findByName(name: String)(implicit session: Session): Option[Season] = {
      Seasons.byName(name).firstOption
    }

    def findByCurrent(current: Boolean)(implicit session: Session): Option[Season] = {
      Seasons.byCurrent(current).firstOption
    }

    def insert(season: Season)(implicit session: Session): Long = {
      Seasons.autoInc.insert((season))
    }

    def update(season: Season)(implicit session: Session) = {
      Seasons.where(_.id === season.id).update(season)
    }

    def delete(seasonId: Long)(implicit session: Session) = {
      Seasons.where(_.id === seasonId).delete
    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options(implicit session: Session): Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  }

}

