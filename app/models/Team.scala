package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


case class Team(id: Option[Long],
                code: String,
                name: String,
                clubId: Long) {
}

object Teams extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[Team] =  DB.withSession {
    implicit session =>
      (for (c <- teams.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      teams.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Team, Club)] = DB.withSession {
    implicit session =>

    val offset = pageSize * page
        val q = (
          for {t <- teams
               c <- t.club
          } yield (t, c)
          ).sortBy(orderField match {
          case 1 => _._1.code.asc
          case -1 => _._1.code.desc
          case 2 => _._1.name.asc
          case -2 => _._1.name.desc
          case 3 => _._2.name.asc
          case -3 => _._2.name.desc
        })
          .drop(offset)
          .take(pageSize)

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[Team] =  DB.withSession {
    implicit session =>
      teams.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[Team] =  DB.withSession {
    implicit session =>
      teams.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[Team] =  DB.withSession {
    implicit session =>
      teams.where(_.code === code).firstOption
  }

  def findByClub(id: Long): Seq[Team] =  DB.withSession {
    implicit session =>
      teams.where(_.clubId === id).list
  }

  def insert(team: Team): Long =  DB.withSession {
    implicit session =>
      teams.insert(team)
  }

  def update(id: Long, team: Team) =  DB.withSession {
    implicit session =>
      val team2update = team.copy(Some(id), team.code, team.name, team.clubId)
      teams.where(_.id === id).update(team2update)
  }

  def delete(teamId: Long) =  DB.withSession {
    implicit session =>
      teams.where(_.id === teamId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- teams
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  def findByClubOptions(id: Long): Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- teams
        if item.clubId is id
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }


}
