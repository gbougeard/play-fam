package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger

case class Match(id: Option[Long],
                 fixtureId: Option[Long],
                 competitionId: Long,
                 eventId: Option[Long])

// define tables
object Matchs extends Table[Match]("fam_match") {

  def id = column[Long]("idMatch", O.PrimaryKey, O.AutoInc)

  def fixtureId = column[Long]("id_fixture")

  def competitionId = column[Long]("id_season_competition")

  def eventId = column[Long]("id_event")

  def * = id.? ~ fixtureId.? ~ competitionId ~ eventId.? <>(Match, Match.unapply _)

  def autoInc = id.? ~ fixtureId.? ~ competitionId ~ eventId.? <>(Match, Match.unapply _) returning id


  // A reified foreign key relation that can be navigated to create a join
  def fixture = foreignKey("FIXTURE_FK", fixtureId, Fixtures)(_.id)

  def competition = foreignKey("COMPETITION_FK", competitionId, SeasonCompetitions)(_.id)

  def event = foreignKey("EVENT_FK", eventId, Events)(_.id)

  val byId = createFinderBy(_.id)
  val byEventId = createFinderBy(_.eventId)

  lazy val pageSize = 10

  def findAll: Seq[Match] = DB.withSession {
    implicit session => {
      (for (c <- Matchs) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      Query(Matchs.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Match, SeasonCompetition, Event)] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session => {
        val matchs = (
          for {m <- Matchs
               if m.eventId.isNotNull
               c <- m.competition
               e <- m.event
          } yield (m, c, e))
          .sortBy(orderField match {
          case 1 => _._1.id.asc
          case -1 => _._1.id.desc
          case 2 => _._3.name.asc
          case -2 => _._3.name.desc
          case 3 => _._3.dtEvent.asc
          case -3 => _._3.dtEvent.desc
        })
          .drop(offset)
          .take(pageSize)

        Page(matchs.list, page, offset, count)
      }
    }
  }

  def findById(id: Long): Option[Match] = DB.withSession {
    implicit session => {
      Matchs.byId(id).firstOption
    }
  }

  def findByEventId(id: Long): Option[Match] = DB.withSession {
    implicit session => {
      Matchs.byEventId(id).firstOption
    }
  }

  def insert(m: Match): Long = DB.withSession {
    implicit session => {
      Matchs.autoInc.insert((m))
    }
  }

  def update(id: Long, m: Match) = DB.withSession {
    implicit session => {
      val match2update = m.copy(Some(id))
      Logger.info("playe2update " + match2update)
      Matchs.where(_.id === id).update(match2update)
    }
  }

  def delete(matchId: Long) = DB.withSession {
    implicit session => {
      Matchs.where(_.id === matchId).delete
    }
  }

  implicit val matchFormat = Json.format[Match]

}