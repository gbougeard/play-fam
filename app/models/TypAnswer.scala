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


object TypAnswer{
  lazy val pageSize = 10

  def findAll: Seq[TypAnswer] =  {
    implicit session:Session => {
      (for (c <- TypAnswers.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(TypAnswers.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypAnswer] = {

    val offset = pageSize * page

     {
      implicit session:Session =>
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

        Page(typAnswers, page, offset, count)
    }
  }

  def findById(id: Long): Option[TypAnswer] =  {
    implicit session:Session => {
      TypAnswers.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[TypAnswer] =  {
    implicit session:Session => {
      TypAnswers.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[TypAnswer] =  {
    implicit session:Session => {
      TypAnswers.byCode(code).firstOption
    }
  }

  def findByGroup(group: String): Option[TypAnswer] =  {
    implicit session:Session => {
      TypAnswers.byGroup(group).firstOption
    }
  }

  def insert(typAnswer: TypAnswer): Long =  {
    implicit session:Session => {
      TypAnswers.autoInc.insert(typAnswer)
    }
  }

  def update(id: Long, typAnswer: TypAnswer) =  {
    implicit session:Session => {
      val typAnswer2update = typAnswer.copy(Some(id), typAnswer.code, typAnswer.name)
      TypAnswers.where(_.id === id).update(typAnswer2update)
    }
  }

  def delete(typAnswerId: Long) =  {
    implicit session:Session => {
      TypAnswers.where(_.id === typAnswerId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {
  //    c <- findAll
  //  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  {
    implicit session:Session =>
      val query = (for {
        item <- TypAnswers
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typAnswerFormat = Json.format[TypAnswer]

}

