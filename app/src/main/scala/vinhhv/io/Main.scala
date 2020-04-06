package vinhhv.io

import vinhhv.io.app.{ AppConfig, SlackApp }
import vinhhv.io.app.SlackApp.SlackApp
import vinhhv.io.client.SlackMethodsClient
import zio.clock.Clock
import zio.{ console => ZConsole, _ }
import zio.console.Console

object Main extends zio.App {
  type AppEnv = Clock with Console with SlackApp
  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program: ZIO[AppEnv, Throwable, Unit] = for {
      _ <- ZConsole.putStrLn("Hello world!")
      _ <- SlackApp.start
    } yield ()

    program
      .provideSomeLayer[ZEnv](createAppLayer)
      .foldM(
          err => ZConsole.putStrLn(s"Oops! ${err.getMessage}") *> ZIO.succeed(1)
        , _ => ZIO.succeed(0)
      )
  }

  def createAppLayer: ZLayer[Any, Throwable, Console with SlackApp] = {
    // TODO: remove redundancy
    val client = AppConfig.live >>> SlackMethodsClient.live
    val server = AppConfig.live ++ client >>> SlackApp.live
    ZConsole.Console.live ++ server
  }
}
