package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger

// Use the implicit threadLocalSession
import scala.slick.session.Database.threadLocalSession

case class Player(id: Option[Long],
                  firstName: String,
                  lastName: String,
                  email: String)

// define tables
object Players extends Table[Player]("fam_player") {

  def id = column[Long]("id_player", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def email = column[String]("email")

  def * = id.? ~ firstName ~ lastName ~ email <>(Player, Player.unapply _)

  def autoInc = id.? ~ firstName ~ lastName ~ email <>(Player, Player.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  // def club = foreignKey("CLUB_FK", clubId, Clubs)(_.id)

  val byId = createFinderBy(_.id)
  val byFirstName = createFinderBy(_.firstName)
  val byLastName = createFinderBy(_.lastName)

  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val pageSize = 10

  def findAll: Seq[Player] = {
    (for (c <- Players.sortBy(_.lastName)) yield c).list
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Player)] = {

    val offset = pageSize * page

    database withSession{
    val players = (
      for {t <- Players
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
      } yield (t)).list

    val totalRows = (for {t <- Players} yield t.id).list.size
    Page(players, page, offset, totalRows)
  }
  }

  def findById(id: Long): Option[Player] = database withSession{
    Players.byId(id).firstOption
  }

  def findByFirstName(firstName: String): Option[Player] =database withSession {
    Players.byFirstName(firstName).firstOption
  }

  def findByLastName(lastName: String): Option[Player] =database withSession {
    Players.byLastName(lastName).firstOption
  }


  def insert(player: Player): Long = database withSession{
    Players.autoInc.insert((player))
  }

  def update(id:Long,player: Player) = database withSession{
    val player2update = player.copy(Some(id))
    Logger.info("playe2update "+player2update)
    Players.where(_.id === id).update(player2update)
  }

  def delete(playerId: Long) = database withSession{
    Players.where(_.id === playerId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)

}