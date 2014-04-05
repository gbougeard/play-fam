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


object Tokens extends DAO {
  // Operations
  def save(token: Token): Token =  DB.withSession {
    implicit session =>
    findByUUID(token.uuid) map { t =>
      tokens.where(_.uuid is t.uuid).update(token)
    } getOrElse {
      tokens.insert(token)
    }
    Logger.debug("saved token %s".format(token))
    token
  }

  def delete(uuid: String) =  DB.withSession {
    implicit session =>
    Logger.debug("delete token %s".format(uuid))
    tokens.where(_.uuid is uuid).mutate(_.delete)
  }

  def deleteAll =  DB.withSession {
    implicit session =>
    tokens.mutate(_.delete)
  }

  def deleteExpiredtokens =  DB.withSession {
    implicit session =>
    tokens.where(_.expirationTime <= DateTime.now).mutate(_.delete)
  }

  // Queries
  def all: List[User] =  DB.withSession {
    implicit session =>
    val q = for (user <- users) yield user
    q.list
  }

  def findByUUID(uuid: String): Option[Token] =  DB.withSession {
    implicit session =>
    Logger.debug("find token %s".format(uuid))
    tokens.where(_.uuid === uuid).firstOption
  }

  implicit val tokenFormat = Json.format[Token]
}