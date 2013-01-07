package models.common

import slick.driver.ExtendedProfile
import models._


/**
 * Created with IntelliJ IDEA.
 * User: gonto
 * Date: 11/23/12
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
class DAL(override val profile: ExtendedProfile) extends UserComponent
with PlayerComponent
with ClubComponent
with TeamComponent
with PlaceComponent
with SeasonComponent
with EventComponent
with Profile {

  import profile.simple._


  def create(implicit session: Session): Unit = {
    Users.ddl.create //helper method to create all tables
  }



}
