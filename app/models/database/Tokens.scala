package models.database

import play.api.db.slick.Config.driver.simple._
import securesocial.core.providers.Token
import com.github.tototoshi.slick.JodaSupport._
import org.joda.time.DateTime
import scala.slick.lifted.Tag
import models.Category

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 20:25
 * To change this template use File | Settings | File Templates.
 */
  class Tokens(tag:Tag) extends Table[Token](tag, "tokens") {

  def uuid = column[String]("uuid", O.PrimaryKey)
  def email = column[String]("email")
  def creationTime = column[DateTime]("creationTime")
  def expirationTime = column[DateTime]("expirationTime")
  def isSignUp = column[Boolean]("isSignUp")

  // Projections
  def * = (
    uuid ,
      email ,
      creationTime ,
      expirationTime ,
      isSignUp )


}
