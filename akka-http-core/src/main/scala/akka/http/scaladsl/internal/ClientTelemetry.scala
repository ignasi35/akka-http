package akka.http.scaladsl.internal

import akka.actor.ActorSystem
import akka.actor.ExtendedActorSystem
import akka.annotation.InternalApi
import akka.annotation.InternalStableApi
import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.stream.scaladsl.Flow

import scala.collection.immutable
import scala.concurrent.Future

/**
 * 
 */
@InternalStableApi
trait ClientTelemetry {
  def instrumenting(clientFlow: Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]]): Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]]
}

/**
 * INTERNAL API
 */
@InternalApi private[http] object ClientTelemetryProvider {
  def start(system: ExtendedActorSystem): ClientTelemetry = {
    if (!system.settings.config.hasPath("akka.http.client.telemetry.implementation")) {
      NoopClientTelemetry
    } else {
      val telemetryFqcn = system.settings.config.getString("akka.projection.telemetry.implementation")
      system.dynamicAccess
        .createInstanceFor[ClientTelemetry](telemetryFqcn, immutable.Seq((classOf[ActorSystem], system)))
        .get
    }
  }

}


/**
 * INTERNAL API
 */
@InternalApi private[http] object NoopClientTelemetry extends ClientTelemetry{
  override def instrumenting(clientFlow : Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]]): Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]] = clientFlow
}


