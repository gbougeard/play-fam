package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class TypAnswer(id: Option[Long],
                     code: String,
                     name: String,
                     group: String)

object TypAnswerJson {
  import play.api.libs.json.Json
  implicit val taJsonFormat = Json.format[TypAnswer]
}

object TypAnswers extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[TypAnswer] =  DB.withSession {
    implicit session =>
      (for (c <- typAnswers.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      typAnswers.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypAnswer] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = for {c <- typAnswers
          .sortBy(typAnswer => orderField match {
          case 1 => typAnswer.code.asc
          case -1 => typAnswer.code.desc
          case 2 => typAnswer.name.asc
          case -2 => typAnswer.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[TypAnswer] =  DB.withSession {
    implicit session =>
      typAnswers.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[TypAnswer] =  DB.withSession {
    implicit session =>
      typAnswers.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[TypAnswer] =  DB.withSession {
    implicit session =>
      typAnswers.where(_.code === code).firstOption
  }

  def findByGroup(group: String): Option[TypAnswer] =  DB.withSession {
    implicit session =>
      typAnswers.where(_.group === group).firstOption
  }

  def insert(typAnswer: TypAnswer): Long =  DB.withSession {
    implicit session =>
      typAnswers.insert(typAnswer)
  }

  def update(id: Long, typAnswer: TypAnswer) =  DB.withSession {
    implicit session =>
      val typAnswer2update = typAnswer.copy(Some(id), typAnswer.code, typAnswer.name)
      typAnswers.where(_.id === id).update(typAnswer2update)
  }

  def delete(typAnswerId: Long) =  DB.withSession {
    implicit session =>
      typAnswers.where(_.id === typAnswerId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {
  //    c <- findAll
  //  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- typAnswers
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}

