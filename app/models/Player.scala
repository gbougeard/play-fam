package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

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

// define tables
object Players extends Table[Player]("fam_player") {

  def id = column[Long]("id_player", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def email = column[String]("email")

  def userId = column[Long]("id_user")

  def * = id.? ~ firstName ~ lastName ~ email ~ userId.? <>(Player, Player.unapply _)

  def autoInc = id.? ~ firstName ~ lastName ~ email ~ userId.? <>(Player, Player.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def user = foreignKey("USER_FK", userId, Users)(_.pid)

  val byId = createFinderBy(_.id)
  val byFirstName = createFinderBy(_.firstName)
  val byLastName = createFinderBy(_.lastName)
  val byUserId = createFinderBy(_.userId)

  lazy val pageSize = 10

  def findAll: Seq[Player] = DB.withSession {
    implicit session:Session => {
      (for (c <- Players.sortBy(_.lastName)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(Players.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Player)] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session:Session => {
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

        Page(players, page, offset, count)
      }
    }
  }

  def findById(id: Long): Option[Player] = DB.withSession {
    implicit session:Session => {
      Players.byId(id).firstOption
    }
  }

  def findByFirstName(firstName: String): Option[Player] = DB.withSession {
    implicit session:Session => {
      Players.byFirstName(firstName).firstOption
    }
  }

  def findByLastName(lastName: String): Option[Player] = DB.withSession {
    implicit session:Session => {
      Players.byLastName(lastName).firstOption
    }
  }

  def findByUserId(id: Long): Option[Player] = DB.withSession {
    implicit session:Session => {
      Players.byUserId(id).firstOption
    }
  }


  def insert(player: Player): Long = DB.withSession {
    implicit session:Session => {
      Players.autoInc.insert((player))
    }
  }

  def update(id: Long, player: Player) = DB.withSession {
    implicit session:Session => {
      val player2update = player.copy(Some(id))
      Logger.info("playe2update " + player2update)
      Players.where(_.id === id).update(player2update)
    }
  }

  def delete(playerId: Long) = DB.withSession {
    implicit session:Session => {
      Players.where(_.id === playerId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)

  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- Players
      } yield (item.id, item.firstName + " " + item.lastName)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val playerFormat = Json.format[Player]
}