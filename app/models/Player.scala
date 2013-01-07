package models

import common.Profile




case class Player(id: Option[Long],
                  firstName: String,
                  lastName: String,
                  email: String)

trait PlayerComponent {
  this: Profile =>

  import profile.simple._



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

    lazy val pageSize = 10

    def findAll(implicit session: Session): Seq[Player] = {
      (for (c <- Players.sortBy(_.lastName)) yield c).list
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(Player)] = {

      val offset = pageSize * page

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

    def findById(id: Long)(implicit session: Session): Option[Player] = {
      Players.byId(id).firstOption
    }

    def findByFirstName(firstName: String)(implicit session: Session): Option[Player] = {
      Players.byFirstName(firstName).firstOption
    }

    def findByLastName(lastName: String)(implicit session: Session): Option[Player] = {
      Players.byLastName(lastName).firstOption
    }


    def insert(player: Player)(implicit session: Session): Long = {
      Players.autoInc.insert((player))
    }

    def update(player: Player)(implicit session: Session) = {
      Players.where(_.id === player.id).update(player)
    }

    def delete(playerId: Long)(implicit session: Session) = {
      Players.where(_.id === playerId).delete
    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options(implicit session: Session): Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)


  }

}