import cats.effect._

import org.http4s._
import org.http4s.implicits._

////////////////////////////////////////////////////////////

class InstanceKindsSuite extends SmartServerSuite {

  test("successful request to /instance-kinds endpoint") {
    val uri = uri"/instance-kinds"
    val req = Request[IO](method = Method.GET, uri = uri)

    serverFixture().httpApp.run(req)
      .map(resp => {
        // note: project spec does not specify what constitutes a valid
		// response body for this endpoint, so we just check the status
        assertEquals(resp.status.code, 200)
		// todo: haven't decided what "gracefully handle errors" means yet,
		// so this test will randomly fail when the smartcloud server returns a 500 error
		// need to rewrite this test to check for graceful error handlign
      })
  }

}