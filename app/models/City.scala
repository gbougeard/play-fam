package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import database.Cities

case class City(id: Option[Long],
                code: String,
                name: String,
                upper: String,
                lower: String,
                provinceId: Long)

object City{
  lazy val pageSize = 10

  def findAll: Seq[City] = DB.withSession {
    implicit session:Session => {
      (for (c <- Cities.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(Cities.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(City, Province)] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session:Session => {
        val cities = (
          for {c <- Cities

               p <- c.province
          } yield (c, p))
        .sortBy(orderField match {
          case 1 => _._1.code.asc
          case -1 => _._1.code.desc
          case 2 => _._1.name.asc
          case -2 => _._1.name.desc
          case 3 => _._2.name.asc
          case -3 => _._2.name.desc
        })
          .drop(offset)
          .take(pageSize)

        val totalRows = count
        Page(cities.list, page, offset, totalRows)
      }
    }
  }

  def findById(id: Long): Option[City] = DB.withSession {
    implicit session:Session => {
      Cities.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[City] = DB.withSession {
    implicit session:Session => {
      Cities.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[City] = DB.withSession {
    implicit session:Session => {
      Cities.byCode(code).firstOption
    }
  }

  def insert(city: City): Long = DB.withSession {
    implicit session:Session => {
      Cities.autoInc.insert(city)
    }
  }

  def update(id: Long, city: City) = DB.withSession {
    implicit session:Session => {
      val city2update = city.copy(Some(id), city.code, city.name, city.upper, city.lower, city.provinceId)
      Cities.where(_.id === id).update(city2update)
    }
  }

  def delete(cityId: Long) = DB.withSession {
    implicit session:Session => {
      Cities.where(_.id === cityId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- Cities
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }


  def json(page: Int, pageSize: Int, orderField: Int): Seq[(City, Province)] = DB.withSession {
    implicit session:Session => {

      println("page " + page)
      println("pageSize " + pageSize)
      println("orderField " + orderField)

      val cities = for {c <- Cities
        .sortBy(city => orderField match {
        case 1 => city.id.asc
        case -1 => city.id.desc
        case 2 => city.code.asc
        case -2 => city.code.desc
        case 3 => city.name.asc
        case -3 => city.name.desc
      })
        .drop(page)
        .take(pageSize)
                        p <- c.province
      } yield (c, p)
      //      Json.toJson(cities.list)
      cities.list

    }
  }

  implicit val cityFormat = Json.format[City]

  import Province._
  implicit val cityWithProvinceReads: Reads[(City, Province)] = (
    (__ \ 'city).read[City] ~
      (__ \ 'province).read[Province]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val cityWithProvinceWrites: Writes[(City, Province)] = (
      (__ \ 'city).write[City] ~
        (__ \ 'province).write[Province]
    ) tupled
  implicit val cityWithsProvinceFormat = Format(cityWithProvinceReads, cityWithProvinceWrites)

}