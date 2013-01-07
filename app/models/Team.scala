package models

import common.Profile
import common.AppDB

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Team(id: Option[Long],
                code: String,
                name: String,
                clubId: Long) {
}

trait TeamComponent {
  this: Profile =>

  import profile.simple._

  implicit val teamFormat = Json.format[Team]

  // define tables
  object Teams extends Table[Team]("fam_team") {

    def id = column[Long]("id_team", O.PrimaryKey, O.AutoInc)

    def name = column[String]("lib_team")

    def code = column[String]("cod_team")

    def clubId = column[Long]("id_club")

    def * = id.? ~ code ~ name ~ clubId <>(Team, Team.unapply _)

    def autoInc = id.? ~ code ~ name ~ clubId <>(Team, Team.unapply _) returning id

    // A reified foreign key relation that can be navigated to create a join
    def club = foreignKey("CLUB_FK", clubId, AppDB.dal.Clubs)(_.id)

    val byId = createFinderBy(_.id)
    val byName = createFinderBy(_.name)
    val byCode = createFinderBy(_.code)

    lazy val pageSize = 10

    def findAll(implicit session: Session): Seq[Team] = {
        (for (c <- Teams.sortBy(_.name)) yield c).list
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(Team, Club)] = {

      val offset = pageSize * page

        val teams = (
          for {t <- Teams
            .sortBy(club => orderField match {
            case 1 => club.code.asc
            case -1 => club.code.desc
            case 2 => club.name.asc
            case -2 => club.name.desc
          })
            .drop(offset)
            .take(pageSize)
               c <- t.club

          } yield (t, c)).list

        val totalRows = (for {t <- Teams} yield t.id).list.size
        Page(teams, page, offset, totalRows)
    }

    def findById(id: Long)(implicit session: Session): Option[Team] =  {
      Teams.byId(id).firstOption
    }

    def findByName(name: String)(implicit session: Session): Option[Team] =  {
      Teams.byName(name).firstOption
    }

    def findByCode(code: String)(implicit session: Session): Option[Team] =  {
      Teams.byCode(code).firstOption
    }

    def insert(team: Team)(implicit session: Session): Long = {
      Teams.autoInc.insert((team))
    }

    def update(team: Team)(implicit session: Session) = {
      Teams.where(_.id === team.id).update(team)
    }

    def delete(teamId: Long)(implicit session: Session) = {
      Teams.where(_.id === teamId).delete
    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options(implicit session: Session): Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)


  }

}