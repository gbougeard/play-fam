/**
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Slick Service by Leon Radley - github.com/leon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package service

import play.api._
import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.UserId
import models._

class SlickUserService(application: Application) extends UserServicePlugin(application) {

  def find(id: UserId): Option[Identity] = Users.findByUserId(id).map {
    u => FamUser.fromUser(u)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = Users.findByEmailAndProvider(email, providerId).map {
    u => FamUser.fromUser(u)
  }

  def save(identity: Identity): Identity = {
    val u = User.fromIdentity(identity)
    Logger.info("sav identity to user %s".format(u))
    FamUser.fromUser(Users.save(u))
  }

  def save(token: Token) {
    Tokens.save(token)
  }

  def findToken(token: String): Option[Token] = Tokens.findByUUID(token)

  def deleteToken(uuid: String) {
    Tokens.delete(uuid)
  }

  def deleteTokens() {
    Tokens.deleteAll()
  }

  def deleteExpiredTokens() {
    Tokens.deleteExpiredTokens()
  }

}
