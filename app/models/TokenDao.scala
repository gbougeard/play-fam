package models

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import securesocial.core._
import securesocial.core.providers.Token

import com.github.tototoshi.slick.JodaSupport._
import org.joda.time.DateTime

import play.api.Play.current

import play.api.libs.json.Json

import play.Logger

import models.database.{Users, Tokens}
/*
  Slick Table for securesocial.core.providers.Token
  case class Token(
    uuid: String,
    email: String,
    creationTime: org.joda.time.DateTime,
    expirationTime: org.joda.time.DateTime,
    isSignUp: Boolean
  )
  */


object TokenDao {
  // Operations
  def save(token: Token): Token = DB.withTransaction { implicit session:Session =>
    findByUUID(token.uuid) map { t =>
      Query(Tokens).where(_.uuid is t.uuid).update(token)
    } getOrElse {
      Tokens.insert(token)
    }
    Logger.debug("saved token %s".format(token))
    token
  }

  def delete(uuid: String) = DB.withTransaction { implicit session:Session =>
    Logger.debug("delete token %s".format(uuid))
    Tokens.where(_.uuid is uuid).mutate(_.delete)
  }

  def deleteAll() = DB.withTransaction { implicit session:Session =>
    Query(Tokens).mutate(_.delete)
  }

  def deleteExpiredTokens() = DB.withTransaction { implicit session:Session =>
    Query(Tokens).where(_.expirationTime <= DateTime.now).mutate(_.delete)
  }

  // Queries
  def all: List[User] = DB.withSession { implicit session:Session =>
    val q = for (user <- Users) yield user
    q.list
  }

  def findByUUID(uuid: String): Option[Token] = DB.withSession { implicit session:Session =>
    def byUUID = Tokens.createFinderBy(_.uuid)
    Logger.debug("find token %s".format(uuid))
    byUUID(uuid).firstOption
  }

  implicit val tokenFormat = Json.format[Token]
}