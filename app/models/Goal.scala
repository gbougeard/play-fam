package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Players._

case class Goal(id: Option[Long],
                matchId: Long,
                teamId: Long,
                strikerId: Option[Long],
                assistId: Option[Long],
                goalTime: Option[Long],
                penalty: Boolean,
                csc: Boolean) {
}

// define tables
object Goals extends Table[Goal]("fam_goal") {

  def id = column[Long]("id_goal", O.PrimaryKey, O.AutoInc)

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def strikerId = column[Long]("id_striker")

  def assistId = column[Long]("id_assist")

  def goalTime = column[Long]("goal_time")

  def penalty = column[Boolean]("penalty")

  def csc = column[Boolean]("csc")

  def * = id.? ~ matchId ~ teamId ~ strikerId.? ~ assistId.? ~ goalTime.? ~ penalty ~ csc <>(Goal, Goal.unapply _)

  def autoInc = id.? ~ matchId ~ teamId ~ strikerId.? ~ assistId.? ~ goalTime.? ~ penalty ~ csc <>(Goal, Goal.unapply _) returning id


  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, Matchs)(_.id)
  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)
  def striker = foreignKey("STRIKER_FK", strikerId, Players)(_.id)
  def assist = foreignKey("ASSIST_FK", assistId, Players)(_.id)

  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam:Long): Seq[(Goal, Option[Player])] = DB.withSession {
    implicit session => {
      val query = (
        for {(g,s) <- Goals leftJoin Players on (_.strikerId === _.id)
             if g.matchId === idMatch
             if g.teamId === idTeam
        } yield (g, s.id.?, s.firstName.?, s.lastName.?, s.email.?))
//      query.list

      query.list.map(row => (row._1, row._2.map(value => Player(Option(value) ,row._3.get, row._4.get, row._5.get)))).sortBy(_._1.goalTime)
    }
  }

  def insert(goal: Goal): Long = DB.withSession {
    implicit session => {
      Goals.autoInc.insert((goal))
    }
  }

  def update(id: Long, goal: Goal) = DB.withSession {
    implicit session => {
      val goal2update = goal.copy(Some(id))
      Goals.where(_.id === id).update(goal2update)
    }
  }

  def delete(goalId: Long) = DB.withSession {
    implicit session => {
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