package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class Group(id: Option[Long],
                 name: String)


// define tables
object Groups extends Table[Group]("fam_group") {

  def id = column[Long]("id_group", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_group")


  def * = id.? ~ name <>(Group, Group.unapply _)

  def autoInc = id.? ~ name <>(Group, Group.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)

  lazy val pageSize = 10

  def findAll: Seq[Group] = DB.withSession {
    implicit session => {
      (for (c <- Groups.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      Query(Groups.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Group] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
        val typCards = (
          for {c <- Groups
            .sortBy(typCard => orderField match {
            case 1 => typCard.name.asc
            case -1 => typCard.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(typCards, page, offset, count)
    }
  }

  def findById(id: Long): Option[Group] = DB.withSession {
    implicit session => {
      Groups.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Group] = DB.withSession {
    implicit session => {
      Groups.byName(name).firstOption
    }
  }


  def insert(typCard: Group): Long = DB.withSession {
    implicit session => {
      Groups.autoInc.insert(typCard)
    }
  }

  def update(id: Long, typCard: Group) = DB.withSession {
    implicit session => {
      val typCard2update = typCard.copy(Some(id), typCard.name)
      Groups.where(_.id === id).update(typCard2update)
    }
  }

  def delete(typCardId: Long) = DB.withSession {
    implicit session => {
      Groups.where(_.id === typCardId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {
  //    c <- findAll
  //  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Groups
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCardFormat = Json.format[Group]

}

