package service

import models.FamUser
import securesocial.core.Identity

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 29/09/13
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
object RequestUtil {
   def getFamUser(user:Identity) :FamUser = {
     user match {
       case u:FamUser => u
       case _ => play.Logger.error(s"User not a FamUser $user")
         throw new IllegalArgumentException()
     }
   }

}
