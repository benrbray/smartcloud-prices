package prices

import cats.effect._
import cats.syntax.semigroupk._
import cats.data.Kleisli

import com.comcast.ip4s._
import fs2.Stream
import scala.concurrent.duration._

import org.http4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.middleware.RetryPolicy
import org.http4s.server.middleware.Logger

import prices.config.Config
import prices.routes.InstanceKindRoutes
import prices.routes.PriceRoutes
import prices.services.SmartcloudClientService
import prices.services.ClientService

////////////////////////////////////////////////////////////

object SmartApp {

  type Routes[F[_]] = Kleisli[F, Request[F], Response[F]]

  def make[F[_]: Sync](
    clientService: ClientService[F]
  ): Routes[F]
    = (
      InstanceKindRoutes[F](clientService).routes
      <+> PriceRoutes[F](clientService).routes
    ).orNotFound

}

class SmartServer(config: Config) {

  // http client with retry-policy in case of server errors
  val httpClientRes = EmberClientBuilder
    .default[IO]
    .withRetryPolicy(RetryPolicy(RetryPolicy.exponentialBackoff(10.seconds, 4)))
    .build
  
  val clientService = SmartcloudClientService.make[IO](
      SmartcloudClientService.Config(
        config.smartcloud.baseUri,
        config.smartcloud.token
      ),
      httpClientRes
    )

  val httpApp = SmartApp.make(clientService)

  val serverRes = EmberServerBuilder
    .default[IO]
    .withHost(Host.fromString(config.app.host).get)
    .withPort(Port.fromInt(config.app.port).get)
    .withHttpApp(Logger.httpApp(true, true)(httpApp))
    .build

  def serve(): Stream[IO, ExitCode] = {
    Stream.eval(serverRes.useForever)
  }

}
