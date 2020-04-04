package vinhhv.io.slack

import vinhhv.io.config.Config.SitrepConfig
import zio.config.Config
import zio.macros.accessible
import zio.{ Has, Task, ZLayer }

@accessible
object SlackApp {
  type SlackApp = Has[Service]

  trait Service {
    def start(path: String, port: Int): Task[Unit]
  }

  def live: ZLayer[Has[Config[SitrepConfig]], Nothing, Has[Service]] =
    ZLayer.fromService(config => Live(config))
}
