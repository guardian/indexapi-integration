
import org.scalatestplus.play._
import services.ConsumerLogicService
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSClient
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws._
import org.scalatest.mock.MockitoSugar

import scala.concurrent.{Await, Future}
import org.mockito.Mockito._
import scala.concurrent.duration._

class CapiFirehoseServiceTest extends PlaySpec with MockitoSugar {

  "An integration test that exercises the whole system" must {
    "Be able to call the lambda" in {

      import scala.concurrent.ExecutionContext.Implicits._

      implicit val system = ActorSystem()
      implicit val materializer = ActorMaterializer()
      val wsClient = AhcWSClient()

      Await.result(
        call(wsClient)
        .andThen { case _ => println("finished")}
        .andThen { case t => println(t.get.json) }
        .andThen { case _ => wsClient.close() }
        .andThen { case _ => system.terminate() },
        5000 millis
      )

      def call(wsClient: WSClient): Future[WSResponse] = {

        val config = mock[play.Configuration]

        when(config.getString("lambdaEndpoint")) thenReturn "https://zynzxqj4zd.execute-api.eu-west-1.amazonaws.com/prod/GoogleIndexAPILambda"

        new ConsumerLogicService(config, wsClient).postUpdate(
          "Global Warning Live From The Front Line",
          "https://www.theguardian.com/environment/live/2017/jan/19/global-warning-live-from-the-climate-change-frontline-as-trump-becomes-president",
          0,
          0
        )

      }

    }
  }
}