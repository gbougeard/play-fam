package metrics

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 30/06/13
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */

trait Instrumented extends nl.grons.metrics.scala.InstrumentedBuilder {
  val metricRegistry = controllers.Application.metricRegistry
}
