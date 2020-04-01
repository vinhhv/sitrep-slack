package vinhhv.io

//import com.slack.api.bolt.App
//import com.slack.api.bolt.jetty.SlackAppServer
import zio.console
import zio.ZIO
import zio.console.Console

object Main extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val program: ZIO[Console, Throwable, Unit] = for {
      _ <- console.putStrLn("Hello world!")
    } yield ()
    program
      .provideLayer(console.Console.live)
      .foldM(
          err => console.putStrLn(s"Sum Ting Wong: ${err.getMessage}") *> ZIO.succeed(1)
        , _ => ZIO.succeed(0)
      )
  }
}
