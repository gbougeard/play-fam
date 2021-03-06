import generators.AnswerGen
import models.Answer

import play.api.libs.json._
import models.AnswerJson._

import org.specs2.ScalaCheck
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import play.api.test.PlaySpecification

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class AnswerSpec extends PlaySpecification with ScalaCheck with AnswerGen {

  "json from(to) iso" ! prop {
    (p: Answer) =>
//          println(s"p: $p")
//    val js = Json.toJson(p)
//          println(s"toJson: ${Json.toJson(p)}")
//          println(s"from(to): ${Json.fromJson(js)}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }
}

