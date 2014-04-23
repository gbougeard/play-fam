import models.Team
import models.TeamJson._


import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck

import play.api.libs.json.{JsSuccess, Json}
import play.api.test.PlaySpecification
import scala.Some

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class TeamSpec extends PlaySpecification with ScalaCheck with TeamGen {
  "json from(to) iso" ! prop {
    (p: Team) =>
//          println(s"p: $p")
//          println(s"toJson: ${Json.toJson(p)}")
//          println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }
}


trait TeamGen {

  lazy val genTeam: Gen[Team] = for {
    id <- arbitrary[Long]
    code <- arbitrary[String]
    name <- arbitrary[String]
    clubId <- arbitrary[Long]
  } yield Team(
      Some(id),
      code,
      name,
      clubId
    )

  implicit lazy val arbTeam: Arbitrary[Team] = Arbitrary(genTeam)
}
