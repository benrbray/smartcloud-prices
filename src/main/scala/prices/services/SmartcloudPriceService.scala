package prices.services

import cats.implicits._
import cats.effect._
import org.http4s._
import org.http4s.circe._

import prices.data._

object SmartcloudPriceService {

  final case class Config(
      baseUri: String,
      token: String
  )

  def make[F[_]: Concurrent](config: Config): PriceService[F] = new SmartcloudPriceService(config)

  private final class SmartcloudPriceService[F[_]: Concurrent](
      config: Config
  ) extends PriceService[F] {

    implicit val pricesEntityDecoder: EntityDecoder[F, List[String]] = jsonOf[F, List[String]]
    
    override def getPrice(kind: InstanceKind): F[PriceInfo] =
      PriceInfo(kind, "TBD").pure[F]

  }

}
