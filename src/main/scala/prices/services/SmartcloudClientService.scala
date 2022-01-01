package prices.services

import cats.implicits._
import cats.effect._
import cats.Applicative

import org.http4s._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.headers._

import io.circe.generic.auto._

import prices.errors._
import prices.data._

////////////////////////////////////////////////////////////

object SmartcloudClientService {

  final case class Config(
      baseUri: String,
      token: String
  )

  def make[F[_]: Concurrent](
    config: Config,
    client: Resource[F,Client[F]]
  ): ClientService[F]
    = new SmartcloudClientService(config, client)

  private final class SmartcloudClientService[F[_]: Concurrent](
      config: Config,
      client: Resource[F,Client[F]]
  ) extends ClientService[F] {

    implicit val instanceKindsEntityDecoder: EntityDecoder[F, List[String]] = jsonOf[F, List[String]]
    implicit val pricesEntityDecoder: EntityDecoder[F, PriceInfo] = jsonOf[F, PriceInfo]

    // endpoint: /instances
    override def instances(): F[List[InstanceKind]] = client.use { client =>
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

    // endpoint: /instances/[KIND]
    override def price(kind: InstanceKind): F[PriceInfo] = client.use { client =>
      // todo: safely convert string to uri with monadic error handling
      val uri = Uri.unsafeFromString(config.baseUri) / "instances" / kind.getString

      // todo: avoid manually constructing auth before every request
      val request = Request[F](
        Method.GET,
        uri=uri,
        headers=Headers(
          Authorization(Credentials.Token(AuthScheme.Bearer, config.token)),
          Accept(MediaType.application.json)
        )
      )

      client
        .expect[PriceInfo](request)
    }
  }
}
