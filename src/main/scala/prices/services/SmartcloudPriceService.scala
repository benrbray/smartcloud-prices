package prices.services

import cats.effect._
import cats.implicits._
import cats.data.EitherT

import io.circe.generic.auto._

import org.http4s._
import org.http4s.circe._
import org.http4s.headers._
import org.http4s.MediaType

import org.http4s.client._

import prices.data._

object SmartcloudPriceService {

  final case class Config(
      baseUri: String,
      token: String
  )

  def make[F[_]: Concurrent](config: Config, client: Resource[F,Client[F]]): PriceService[F] 
    = new SmartcloudPriceService(config, client)

  private final class SmartcloudPriceService[F[_]: Concurrent](
      config: Config,
      httpClient: Resource[F,Client[F]]
  ) extends PriceService[F] {

    implicit val pricesEntityDecoder: EntityDecoder[F, PriceInfo] = jsonOf[F, PriceInfo]
    
    override def getPrice(kind: InstanceKind): F[PriceInfo] = httpClient.use { client =>
      // todo: safely convert string to uri with monadic error handling
      val uri = Uri.unsafeFromString(config.baseUri) / "instances" / kind.getString

      val auth = Authorization(Credentials.Token(AuthScheme.Bearer, config.token))
      val accept = Accept(MediaType.application.json)

      val request = Request[F](
        Method.GET,
        uri=uri,
        headers=Headers(auth, accept)
      )

      client.expect[PriceInfo](request)
    }

  }

}
