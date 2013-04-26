package models

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import securesocial.core._
import securesocial.core.providers.Token

import _root_.java.sql.Date
import org.joda.time.DateTime

import play.api.Play.current

import play.api.libs.json.Json

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


object Tokens extends Table[Token]("tokens") {

  // Conversions for JodaTime
  implicit def date2dateTime = MappedTypeMapper.base[DateTime, Date] (
    dateTime => new Date(dateTime.getMillis),
    date => new DateTime(date)
  )

  def uuid = column[String]("uuid", O.PrimaryKey)
  def email = column[String]("email")
  def creationTime = column[DateTime]("creationTime")
  def expirationTime = column[DateTime]("expirationTime")
  def isSignUp = column[Boolean]("isSignUp")

  // Projections
  def * = {
    uuid ~
      email ~
      creationTime ~
      expirationTime ~
      isSignUp <> (Token.apply _, Token.unapply _)
  }

  // Operations
  def save(token: Token): Token = DB.withTransaction { implicit session =>
    findByUUID(token.uuid) map { t =>
      Query(Tokens).where(_.uuid is t.uuid).update(token)
    } getOrElse {
      this.insert(token)
    }
    token
  }

  def delete(uuid: String) = DB.withTransaction { implicit session =>
    this.where(_.uuid is uuid).mutate(_.delete)
  }

  def deleteAll() = DB.withTransaction { implicit session =>
    Query(Tokens).mutate(_.delete)
  }

  def deleteExpiredTokens() = DB.withTransaction { implicit session =>
    Query(Tokens).where(_.expirationTime <= DateTime.now).mutate(_.delete)
  }

  // Queries
  def all: List[User] = DB.withSession { implicit session =>
    val q = for (user <- Users) yield user
    q.list
  }

  def findByUUID(uuid: String): Option[Token] = DB.withSession { implicit session =>
    def byUUID = createFinderBy(_.uuid)
    byUUID(uuid).firstOption
  }

  implicit val tokenFormat = Json.format[Token]
}