package play.api.db.slick.joda

import com.github.tototoshi.slick.GenericJodaSupport

/**
 * Created by gbougeard on 22/04/14.
 */
object PlayJodaSupport extends GenericJodaSupport(play.api.db.slick.Config.driver)
