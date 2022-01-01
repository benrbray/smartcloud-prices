package prices.services

import cats.implicits._
import cats.effect._
import cats.Applicative

import org.http4s._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.headers._

import prices.errors._
import prices.data._

object SmartcloudInstanceKindService {

  final case class Config(
      baseUri: String,
      token: String
  )

  def make[F[_]: Concurrent](
    config: Config,
    client: Resource[F,Client[F]]
  ): InstanceKindService[F]
    = new SmartcloudInstanceKindService(config, client)

  private final class SmartcloudInstanceKindService[F[_]: Concurrent](
      config: Config,
      client: Resource[F,Client[F]]
  ) extends InstanceKindService[F] {

    implicit val instanceKindsEntityDecoder: EntityDecoder[F, List[String]] = jsonOf[F, List[String]]

    override def getAll(): F[List[InstanceKind]] = client.use { client =>
      // todo: safely convert string to uri with monadic error handling
      val uri = Uri.unsafeFromString(config.baseUri) / "instances"

      // todo: avoid manually constructing auth before every request
      val request = Request[F](
        Method.GET,
        uri=uri,
        headers=Headers(
          Authorization(Credentials.Token(AuthScheme.Bearer, config.token)),
          Accept(MediaType.application.json)
        )
      )

      client.expect[List[String]](request)
        .map(x => x.map(InstanceKind(_)))
    }

  }

}
