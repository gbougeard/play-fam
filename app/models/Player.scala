package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger
import database.Players
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import scala.Some

case class Player(id: Option[Long],
                  firstName: String,
                  lastName: String,
                  email: String,
                  userId:Option[Long]){
  def displayName : String = {
    firstName + " " + lastName
  }
}

object Players extends DAO{
  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[Player] =  {
      (for (c <- players.sortBy(_.lastName)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      players.length.run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(Player)] = {

    val offset = pageSize * page

        val players = (
          for {t <- players
            .sortBy(player => orderField match {
            case 1 => player.firstName.asc
            case -1 => player.firstName.desc
            case 2 => player.lastName.asc
            case -2 => player.lastName.desc
            case 3 => player.email.asc
            case -3 => player.email.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield t).list

        Page(players, page, offset, count)
  }

  def findById(id: Long)(implicit session: Session): Option[Player] =  {
      players.where(_.id === id).firstOption
  }

  def findByFirstName(firstName: String)(implicit session: Session): Option[Player] =  {
      players.where(_.firstName === firstName).firstOption
  }

  def findByLastName(lastName: String)(implicit session: Session): Option[Player] =  {
      players.where(_.lastName === lastName).firstOption
  }

  def findByUserId(id: Long)(implicit session: Session): Option[Player] =  {
      players.where(_.userId === id).firstOption
  }


  def insert(player: Player)(implicit session: Session): Long =  {
      players.insert(player)
  }

  def update(id: Long, player: Player)(implicit session: Session) =  {
      val player2update = player.copy(Some(id))
      Logger.info("playe2update " + player2update)
      players.where(_.id === id).update(player2update)
  }

  def delete(playerId: Long)(implicit session: Session) =  {
      players.where(_.id === playerId).delete
  }

  def find(filter: String)(implicit session: Session) =  {
      play.Logger.debug(s"players.find $filter")
      val q1 = players.filter(_.firstName.toUpperCase like s"%${filter.toUpperCase}%")
      val q2 = players.filter(_.lastName.toUpperCase like s"%${filter.toUpperCase}%")
      val unionQuery = q1 union q2
      unionQuery.list
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)

  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- players
      } yield (item.id, item.firstName + " " + item.lastName)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val playerFormat = Json.format[Player]
}

trait PlayerGen {

  lazy val genPlayer: Gen[Player] = for {
    id <- arbitrary[Long]
    firstname <- arbitrary[String]
    lastname <- arbitrary[String]
    email <- arbitrary[String]
    userid <- arbitrary[Long]
  } yield Player(
      Some(id),
      firstname,
      lastname,
      email,
      Some(userid))

  implicit lazy val arbPlayer: Arbitrary[Player] = Arbitrary(genPlayer)
}