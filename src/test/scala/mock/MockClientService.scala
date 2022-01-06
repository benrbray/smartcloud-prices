package mock

import cats.implicits._
import cats.effect._

import _root_.prices.data._
import _root_.prices.services.ClientService

////////////////////////////////////////////////////////////

object MockClientService {

  /**
   * Returns a new MockClientService that serves up dummy data.
   */
  def make[F[_]: Concurrent](
    mockData: List[PriceInfo]
  ): ClientService[F]
    = new MockClientService(mockData)

  private final class MockClientService[F[_]: Concurrent](
      mockData: List[PriceInfo],
  ) extends ClientService[F] {

    // endpoint: /instances
    override def instances(): F[List[InstanceKind]] = {
      mockData.map(info => InstanceKind(info.kind)).pure[F]
    }

    // endpoint: /instances/[KIND]
    override def price(kind: InstanceKind): F[PriceInfo] = {
      // todo: return Option[PriceInfo]
      mockData.find(info => info.kind == kind.kind).get.pure[F]
    }
  }
}
