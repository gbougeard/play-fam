package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


case class Goal(id: Option[Long],
                matchId: Long,
                teamId: Long,
                strikerId: Option[Long],
                assistId: Option[Long],
                goalTime: Option[Long],
                penalty: Boolean,
                csc: Boolean)

object GoalJson {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val goalJsonFormat = Json.format[Goal]

  import models.PlayerJson._

  implicit val goalCompleteReads: Reads[(Goal, Option[Player])] = (
    (__ \ 'goal).read[Goal] ~
      (__ \ 'striker).readNullable[Player]
    ) tupled

  implicit val goalCompleteWrites: Writes[(Goal, Option[Player])] = (
    (__ \ 'goal).write[Goal] ~
      (__ \ 'striker).write[Option[Player]]
    ) tupled
}

object Goals extends DAO{
  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Seq[(Goal, Option[Player])] =  DB.withSession {
    implicit session =>
      val query = for {(g, s) <- goals leftJoin players on (_.strikerId === _.id)
                       if g.matchId === idMatch
                       if g.teamId === idTeam
      } yield (g, s.id.?, s.firstName.?, s.lastName.?, s.email.?, s.userId.?)

      query.list.map(row => (row._1, row._2.map(value => Player(Option(value), row._3.get, row._4.get, row._5.get, row._6)))).sortBy(_._1.goalTime)
  }

  def insert(goal: Goal): Long =  DB.withSession {
    implicit session =>
      goals.insert(goal)
  }

  def update(id: Long, goal: Goal) =  DB.withSession {
    implicit session =>
      val goal2update = goal.copy(Some(id))
      goals.where(_.id === id).update(goal2update)
  }

  def delete(goalId: Long) =  DB.withSession {
    implicit session =>
      goals.where(_.id === goalId).delete
  }

}