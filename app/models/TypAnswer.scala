package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.TypAnswers

case class TypAnswer(id: Option[Long],
                     code: String,
                     name: String,
                     group: String)


object TypAnswers extends DAO{
  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[TypAnswer] =  {
      (for (c <- typAnswers.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      (typAnswers.length).run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[TypAnswer] = {

    val offset = pageSize * page

        val typAnswers = (
          for {c <- typAnswers
            .sortBy(typAnswer => orderField match {
            case 1 => typAnswer.code.asc
            case -1 => typAnswer.code.desc
            case 2 => typAnswer.name.asc
            case -2 => typAnswer.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(typAnswers, page, offset, count)
  }

  def findById(id: Long)(implicit session: Session): Option[TypAnswer] =  {
      typAnswers.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[TypAnswer] =  {
      typAnswers.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[TypAnswer] =  {
      typAnswers.where(_.code === code).firstOption
  }

  def findByGroup(group: String)(implicit session: Session): Option[TypAnswer] =  {
      typAnswers.where(_.group === group).firstOption
  }

  def insert(typAnswer: TypAnswer)(implicit session: Session): Long =  {
      typAnswers.insert(typAnswer)
  }

  def update(id: Long, typAnswer: TypAnswer)(implicit session: Session) =  {
      val typAnswer2update = typAnswer.copy(Some(id), typAnswer.code, typAnswer.name)
      typAnswers.where(_.id === id).update(typAnswer2update)
  }

  def delete(typAnswerId: Long)(implicit session: Session) =  {
      typAnswers.where(_.id === typAnswerId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {
  //    c <- findAll
  //  } yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- typAnswers
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typAnswerFormat = Json.format[TypAnswer]

}

