package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Player._
import database.Goals
import database.Players

case class Goal(id: Option[Long],
                matchId: Long,
                teamId: Long,
                strikerId: Option[Long],
                assistId: Option[Long],
                goalTime: Option[Long],
                penalty: Boolean,
                csc: Boolean) {
}
object Goal{
  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Seq[(Goal, Option[Player])] =  {
    implicit session:Session => {
      val query = for {(g, s) <- Goals leftJoin Players on (_.strikerId === _.id)
                       if g.matchId === idMatch
                       if g.teamId === idTeam
      } yield (g, s.id.?, s.firstName.?, s.lastName.?, s.email.?, s.userId.?)

      query.list.map(row => (row._1, row._2.map(value => Player(Option(value), row._3.get, row._4.get, row._5.get, row._6)))).sortBy(_._1.goalTime)
    }
  }

  def insert(goal: Goal): Long =  {
    implicit session:Session => {
      Goals.autoInc.insert(goal)
    }
  }

  def update(id: Long, goal: Goal) =  {
    implicit session:Session => {
      val goal2update = goal.copy(Some(id))
      Goals.where(_.id === id).update(goal2update)
    }
  }

  def delete(goalId: Long) =  {
    implicit session:Session => {
      Goals.where(_.id === goalId).delete
    }
  }


  implicit val goalFormat = Json.format[Goal]

  implicit val goalCompleteReads: Reads[(Goal, Option[Player])] = (
    (__ \ 'goal).read[Goal] ~
      (__ \ 'striker).readNullable[Player]
    ) tupled

  implicit val goalCompleteWrites: Writes[(Goal, Option[Player])] = (
    (__ \ 'goal).write[Goal] ~
      (__ \ 'striker).write[Option[Player]]
    ) tupled

}