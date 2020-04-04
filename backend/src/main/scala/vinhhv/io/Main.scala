package vinhhv.io

import vinhhv.io.config.Config
import vinhhv.io.config.Config.SitrepConfig
import vinhhv.io.slack.SlackApp
import zio.clock.Clock
import zio.{ console => ZConsole, _ }
import zio.config.{ Config => ZIOConfig }
import zio.console.Console

object Main extends zio.App {
  type AppEnv      = Clock with Console
  type HttpTask[A] = RIO[AppEnv, A]
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val program: ZIO[AppEnv, Throwable, Unit] = for {
      _ <- ZConsole.putStrLn("Hello world!")
      config = Config.live
      slack  = config >>> SlackApp.live
      server <- ZIO.runtime[AppEnv].flatMap { _ =>
        slack.
      }
    } yield server

    program
      .provideSomeLayer[ZEnv](ZConsole.Console.live)
      .foldM(
          err => ZConsole.putStrLn(s"Oops! ${err.getMessage}") *> ZIO.succeed(1)
        , _ => ZIO.succeed(0)
      )
  }

  val slackLayer: URLayer[Has[ZIOConfig[SitrepConfig]], Has[Task[Unit]]] =
    ZLayer.fromService[ZIOConfig[SitrepConfig], Task[Unit]] { configZ =>
      val config = configZ.get.config.slackAppConfig
      SlackApp(config).start(config.path, config.port)
    }
}
