package vinhhv.io.slack

import vinhhv.io.config.Config.SitrepConfig
import zio.config.Config
import zio.macros.accessible
import zio.{ Has, Task, URLayer, ZLayer }

@accessible
object SlackApp {
  type SlackApp = Has[Service]

  trait Service {
    def start: Task[Unit]
  }

  def live: URLayer[Has[Config[SitrepConfig]], Has[Service]] =
    ZLayer.fromService(config => Live(config))
}
