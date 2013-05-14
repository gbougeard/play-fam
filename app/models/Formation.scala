package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class Formation(id: Option[Long],
                     code: String,
                     name: String,
                     isDefault: Boolean,
                     typMatchId: Long)


// define tables
object Formations extends Table[Formation]("fam_formation") {

  def id = column[Long]("id_formation", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_formation")

  def code = column[String]("cod_formation")

  def isDefault = column[Boolean]("byDefault")

  def typMatchId = column[Long]("id_typ_match")

  def * = id.? ~ code ~ name ~ isDefault ~ typMatchId <>(Formation, Formation.unapply _)

  def autoInc = id.? ~ code ~ name ~ isDefault ~ typMatchId <>(Formation, Formation.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

  lazy val pageSize = 10

  def findAll: Seq[Formation] = DB.withSession {
    implicit session => {
      (for (c <- Formations.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      Query(Formations.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Formation, TypMatch)] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
        val formations = (
          for {c <- Formations
            .sortBy(formation => orderField match {
            case 1 => formation.code.asc
            case -1 => formation.code.desc
            case 2 => formation.name.asc
            case -2 => formation.name.desc
          })

            .drop(offset)
            .take(pageSize)
               tm <- c.typMatch
          } yield (c, tm)).list

        Page(formations, page, offset, count)
    }
  }

  def findById(id: Long): Option[Formation] = DB.withSession {
    implicit session => {
      Formations.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Formation] = DB.withSession {
    implicit session => {
      Formations.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Formation] = DB.withSession {
    implicit session => {
      Formations.byCode(code).firstOption
    }
  }

  def insert(formation: Formation): Long = DB.withSession {
    implicit session => {
      Formations.autoInc.insert((formation))
    }
  }

  def update(id: Long, formation: Formation) = DB.withSession {
    implicit session => {
      val formation2update = formation.copy(Some(id), formation.code, formation.name)
      Formations.where(_.id === id).update(formation2update)
    }
  }

  def delete(formationId: Long) = DB.withSession {
    implicit session => {
      Formations.where(_.id === formationId).delete
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
        item <- Formations
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val formationFormat = Json.format[Formation]

}

