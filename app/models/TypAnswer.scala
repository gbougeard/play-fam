package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class TypAnswer(id: Option[Long],
                     code: String,
                     name: String,
                     group: String)


// define tables
object TypAnswers extends Table[TypAnswer]("fam_typ_answer") {

  def id = column[Long]("id_typ_answer", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_answer")

  def code = column[String]("cod_typ_answer")

  def group = column[String]("grp_typ_answer")

  def * = id.? ~ code ~ name ~ group <>(TypAnswer, TypAnswer.unapply _)

  def autoInc = id.? ~ code ~ name ~ group <>(TypAnswer, TypAnswer.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byGroup = createFinderBy(_.group)

  lazy val pageSize = 10

  def findAll: Seq[TypAnswer] = DB.withSession {
    implicit session => {
      (for (c <- TypAnswers.sortBy(_.name)) yield c).list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypAnswer] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
        val typAnswers = (
          for {c <- TypAnswers
            .sortBy(typAnswer => orderField match {
            case 1 => typAnswer.code.asc
            case -1 => typAnswer.code.desc
            case 2 => typAnswer.name.asc
            case -2 => typAnswer.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        val totalRows = (for (c <- TypAnswers) yield c.id).list.size
        Page(typAnswers, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[TypAnswer] = DB.withSession {
    implicit session => {
      TypAnswers.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[TypAnswer] = DB.withSession {
    implicit session => {
      TypAnswers.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[TypAnswer] = DB.withSession {
    implicit session => {
      TypAnswers.byCode(code).firstOption
    }
  }

  def findByGroup(group: String): Option[TypAnswer] = DB.withSession {
    implicit session => {
      TypAnswers.byGroup(group).firstOption
    }
  }

  def insert(typAnswer: TypAnswer): Long = DB.withSession {
    implicit session => {
      TypAnswers.autoInc.insert((typAnswer))
    }
  }

  def update(id: Long, typAnswer: TypAnswer) = DB.withSession {
    implicit session => {
      val typAnswer2update = typAnswer.copy(Some(id), typAnswer.code, typAnswer.name)
      TypAnswers.where(_.id === id).update(typAnswer2update)
    }
  }

  def delete(typAnswerId: Long) = DB.withSession {
    implicit session => {
      TypAnswers.where(_.id === typAnswerId).delete
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
        item <- TypAnswers
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typAnswerFormat = Json.format[TypAnswer]

}

