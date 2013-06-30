/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 23/04/13
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */

import play.api.Application
import play.api.mvc._
import play.Logger

object Global extends WithFilters(AccessLog) {

  /** The application wide metrics registry. */


  override def onStart(app: Application) {
    super.onStart(app)
    Logger.info("Starting app")
  }

  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info("Shhutdown app")
  }
}

object AccessLog extends Filter {

  override def apply(next: RequestHeader => Result)(request: RequestHeader): Result = {
    val result = next(request)
    //    play.Logger.info(request + "\n\t => " + result)
    result
  }
}


