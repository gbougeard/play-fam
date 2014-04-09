package binders

import play.api.mvc._
import java.net.URLEncoder
import play.api.data.Mapping
import play.api.data.Forms._
import org.joda.time.DateTime

// --- Definition of a query string binder for type A using a Mapping[A]
// https://gist.github.com/julienrf/2503029

object Binders {
  implicit def mappingBinder[A](implicit mapping: Mapping[A]) = new QueryStringBindable[A] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, A]] = {
      val data = for {
        (k, ps) <- params
        if k startsWith key
        p <- ps.headOption
      } yield (k.drop(key.length + 1), p)
      if (data.isEmpty) {
        None
      } else {
        Some(mapping.bind(data.toMap).left.map(_ => "Unable to bind object for key '%s'".format(key)))
      }
    }

    override def unbind(key: String, value: A): String = {
      val (map, _) = mapping.unbind(value)
      map.map {
        case (k, v) => key + "." + k + "=" + URLEncoder.encode(v, "utf-8")
      }.mkString("&")
    }
  }
}

case class BookingQS(custId: Option[Long],
                     hotelId: Option[Long],
                     statuses: List[String],
                     fromDate: Option[DateTime],
                     toDate: Option[DateTime])

object BookingQS {
  implicit val userMapping: Mapping[BookingQS] =
    mapping(
      "custId" -> optional(longNumber),
      "hotelId" -> optional(longNumber),
      "statuses" -> list(text),
      "fromDate" -> optional(jodaDate("yyyy-MM-dd")),
      "toDate" -> optional(jodaDate("yyyy-MM-dd"))
    )(BookingQS.apply)(BookingQS.unapply)

  def empty: BookingQS = {
    new BookingQS(None, None, List(), None, None)
  }
}
