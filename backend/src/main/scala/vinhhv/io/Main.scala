package vinhhv.io

import vinhhv.io.config.Config
import vinhhv.io.slack.SlackApp
import zio.clock.Clock
import zio.{ console => ZConsole, _ }
import zio.console.Console

object Main extends zio.App {
  type AppEnv      = Clock with Console
  type HttpTask[A] = RIO[AppEnv, A]
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val program: ZIO[AppEnv, Throwable, Unit] = for {
      _      <- ZConsole.putStrLn("Hello world!")
      config <- Config.loadConfig
      server <- ZIO.runtime[AppEnv].flatMap { _ =>
        SlackApp(config).start(config.path, config.port)
      }
    } yield server

    program
      .provideSomeLayer[ZEnv](ZConsole.Console.live)
      .foldM(
          err => ZConsole.putStrLn(s"Sum Ting Wong: ${err.getMessage}") *> ZIO.succeed(1)
        , _ => ZIO.succeed(0)
      )
  }
}
