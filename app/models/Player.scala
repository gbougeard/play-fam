package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.Logger

case class Player(id: Option[Long],
                  firstName: String,
                  lastName: String,
                  email: String,
                  userId:Option[Long]){
  def displayName : String = {
    firstName + " " + lastName
  }
}

object PlayerJson {
  import play.api.libs.json.Json
  implicit val playerJsonFormat = Json.format[Player]
}

object Players extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[Player] =  DB.withSession {
    implicit session =>
      (for (c <- players.sortBy(_.lastName)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      players.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Player)] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = for {t <- players
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
        } yield t

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[Player] =  DB.withSession {
    implicit session =>
      players.where(_.id === id).firstOption
  }

  def findByFirstName(firstName: String): Option[Player] =  DB.withSession {
    implicit session =>
      players.where(_.firstName === firstName).firstOption
  }

  def findByLastName(lastName: String): Option[Player] =  DB.withSession {
    implicit session =>
      players.where(_.lastName === lastName).firstOption
  }

  def findByUserId(id: Long): Option[Player] =  DB.withSession {
    implicit session =>
      players.where(_.userId === id).firstOption
  }


  def insert(player: Player): Long =  DB.withSession {
    implicit session =>
      players.insert(player)
  }

  def update(id: Long, player: Player) =  DB.withSession {
    implicit session =>
      val player2update = player.copy(Some(id))
      Logger.info("playe2update " + player2update)
      players.where(_.id === id).update(player2update)
  }

  def delete(playerId: Long) =  DB.withSession {
    implicit session =>
      players.where(_.id === playerId).delete
  }

  def find(filter: String) =  DB.withSession {
    implicit session =>
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

  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- players
      } yield (item.id, item.firstName + " " + item.lastName)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}
