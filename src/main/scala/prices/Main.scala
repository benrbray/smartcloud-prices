package prices

import cats.effect.{ IO, IOApp }

import prices.config.Config

object Main extends IOApp.Simple {

  def run: IO[Unit] = Config.load[IO].flatMap(config => {
    val server = new SmartServer(config)
    server.serve().compile.drain
  })

}
