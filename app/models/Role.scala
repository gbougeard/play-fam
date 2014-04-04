package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.Logger
import scala.util.Try
import play.api.cache.Cache

import service.{Administrator, Permission}

import play.api.libs.json._
import database.Roles

case class Role(userId: Long,
                groupId: Long)

object Roles extends DAO{
  lazy val pageSize = 10

  //  def findPage(page: Int = 0, orderField: Int): Page[(Player)] = {
  //
  //    val offset = pageSize * page
  //
  //     {
  //      implicit session:Session => {
  //        val players = (
  //          for {t <- Players
  //            .sortBy(player => orderField match {
  //            case 1 => player.firstName.asc
  //            case -1 => player.firstName.desc
  //            case 2 => player.lastName.asc
  //            case -2 => player.lastName.desc
  //            case 3 => player.email.asc
  //            case -3 => player.email.desc
  //          })
  //            .drop(offset)
  //            .take(pageSize)
  //          } yield (t)).list
  //
  //        val totalRows = (for {t <- Players} yield t.id).list.size
  //        Page(players, page, offset, totalRows)
  //      }
  //    }
  //  }

  def findByUserId(id: Long)(implicit session: Session): Seq[String] =  {
      play.Logger.debug(s"roles.findByUserId $id")
      val query = for {r <- roles
                       if r.userId === id
                       g <- r.group

      } yield g.name
      query.list
  }

  def findByGroupId(id: Long)(implicit session: Session): Seq[(Role, User, Group)] =  {
      val query = for {r <- roles
                       if r.groupId === id
                       g <- r.group
                       u <- r.user

      } yield (r, u, g)
      query.list
  }


  def insert(role: Role)(implicit session: Session): Int =  {
      Logger.debug("insert %s".format(role))
      roles.insert(role)
  }

  def insert(roles: Seq[Role])(implicit session: Session): Try[Option[Int]] =  {
      Try(roles.insertAll(roles: _*))
  }

  def delete(id: Long)(implicit session: Session) =  {
      roles.where(_.userId === id).delete
  }

  def isUserInRole(userId: Long, permissions: Set[Permission]): Boolean = {
    play.Logger.debug(s"isUserInRole - Withroles getting roles from cache for roles.$userId")
    val rolesCached = Cache.get(s"roles.$userId")
    play.Logger.debug(s"isUserInRole - Withroles $permissions : $rolesCached")
    rolesCached match {
      case Some(r) => {
        play.Logger.debug(s"isUserInRole - $r")
        val roles = Json.parse(r.toString).as[Set[String]].map(Permission.valueOf)
        (!roles.intersect(permissions).isEmpty) || roles.contains(Administrator)
      }
      case None => false
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)

  //  def options: Seq[(String, String)] =  {
  //    implicit session:Session =>
  //      val query = (for {
  //        item <- Players
  //      } yield (item.id, item.firstName + " " + item.lastName)
  //        ).sortBy(_._2)
  //      query.list.map(row => (row._1.toString, row._2))
  //  }

  //  implicit val playerSeasonFormat = Json.format[PlayerSeason]
}