package prices

import cats.effect._
import com.comcast.ip4s._
import fs2.Stream
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.server.middleware.Logger
import cats.syntax.semigroupk._

import prices.config.Config
import prices.routes.InstanceKindRoutes
import prices.services.SmartcloudInstanceKindService
import prices.routes.PriceRoutes
import prices.services.SmartcloudPriceService

class SmartServer(config: Config) {

  val instanceKindService = SmartcloudInstanceKindService.make[IO](
      SmartcloudInstanceKindService.Config(
        config.smartcloud.baseUri,
        config.smartcloud.token
      )
    )

  val httpClientRes = EmberClientBuilder.default[IO].build

  val priceService = SmartcloudPriceService.make[IO](
    SmartcloudPriceService.Config(
      config.smartcloud.baseUri,
      config.smartcloud.token
    ),
    httpClientRes
  )

  val httpApp = (
    InstanceKindRoutes[IO](instanceKindService).routes
    <+> PriceRoutes[IO](priceService).routes
  ).orNotFound

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
