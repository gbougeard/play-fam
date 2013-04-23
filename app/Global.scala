/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 23/04/13
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
import play.api.mvc._

object Global extends WithFilters(AccessLog)

object AccessLog extends Filter {
  override def apply(next: RequestHeader => Result)(request: RequestHeader): Result = {
    val result = next(request)
//    play.Logger.info(request + "\n\t => " + result)
    result
  }
}
