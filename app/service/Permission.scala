package service

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 28/08/13
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */
sealed trait Permission
case object Administrator extends Permission
case object Player extends Permission
case object Coach extends Permission
case object NormalUser extends Permission

object Permission {

  def valueOf(value: String): Permission = value match {
    case "Administrator" => Administrator
    case "Player"    => Player
    case "Coach"    => Coach
    case _ => NormalUser //throw new IllegalArgumentException()
  }

}
