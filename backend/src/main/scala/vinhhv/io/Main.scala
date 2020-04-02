package vinhhv.io

//import com.slack.api.bolt.App
//import com.slack.api.bolt.jetty.SlackAppServer
import cats.effect.ExitCode
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import vinhhv.io.http.Api
import zio.clock.Clock
import zio.{ console => ZConsole, _ }
import zio.console.Console
import zio.interop.catz._

object Main extends zio.App {
  type AppEnv      = Clock with Console
  type HttpTask[A] = RIO[AppEnv, A]
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val program: ZIO[AppEnv, Throwable, Unit] = for {
      _ <- ZConsole.putStrLn("Hello world!")
      httpApp = Router[HttpTask]("/status" -> Api().routes).orNotFound
      server <- ZIO.runtime[AppEnv].flatMap { implicit rts =>
        BlazeServerBuilder[HttpTask]
          .bindHttp(8080, "127.0.0.1")
          .withHttpApp(CORS(httpApp))
          .serve
          .compile[HttpTask, HttpTask, ExitCode]
          .drain
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
