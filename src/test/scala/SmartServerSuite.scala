import cats.effect._

import prices.config.Config
import prices.SmartServer

import munit.CatsEffectSuite

////////////////////////////////////////////////////////////

trait SmartServerSuite extends CatsEffectSuite {

  val serverFixture = ResourceSuiteLocalFixture(
    "server",
    Resource.make
      (Config.load[IO].map(config => 
          new SmartServer(config))
      )(_ => IO.unit)
  )

  abstract override def munitFixtures = super.munitFixtures ++ List(serverFixture)

}