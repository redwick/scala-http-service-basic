package server

import com.typesafe.config.ConfigFactory
import io.circe.syntax.EncoderOps
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.cors.scaladsl.CorsDirectives._
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.util.Timeout
import org.slf4j.LoggerFactory
import server.app.Codes
import java.util.{Calendar, TimeZone}
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, SECONDS}

object HttpManager extends Codes {

  private val logger = LoggerFactory.getLogger("server/http")
  private val config = ConfigFactory.load()


  def apply(system: ActorSystem[Nothing]): Future[Http.ServerBinding] = {
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))
      implicit val sys: ActorSystem[Nothing] = system
      implicit val timeout: Timeout = Duration(5, SECONDS)
      val route: Route = cors() {
        concat(
          basicRoutes(),
        )
      }
      logger.info("http started at " + config.getString("http.host") + ":" + config.getString("http.port"))
      Http().newServerAt(config.getString("http.host"), config.getInt("http.port")).bind(route)
    }
    catch {
      case e: Throwable =>
        println(e.toString)
        Thread.sleep(5 * 1000)
        HttpManager(system)
    }
  }


  private def basicRoutes(): Route = {
    concat(
      (get & path("time")) {
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, Calendar.getInstance().getTime.toString.asJson.noSpaces)))
      },
      (get & path("time-text")) {
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, Calendar.getInstance().getTime.toString)))
      },
      (get & path("time-json")) {
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, Calendar.getInstance().getTime.toString.asJson.noSpaces)))
      },
    )
  }

}
