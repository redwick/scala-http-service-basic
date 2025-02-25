package server

import com.typesafe.config.ConfigFactory
import org.apache.pekko.NotUsed
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorSystem, Behavior}
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main{

  private val logger = LoggerFactory.getLogger("master")
  private val config = ConfigFactory.load()

  def main(args: Array[String]): Unit = {
    try {
      Await.result(ActorSystem(Main(), "Main").whenTerminated, Duration.Inf)
    } catch {
      case e: Throwable =>
        logger.error(e.toString)
        main(Array.empty[String])
    }
  }
  def apply(): Behavior[NotUsed] = {
    Behaviors.setup { context =>
      HttpManager(context.system)
      Behaviors.empty
    }
  }
}