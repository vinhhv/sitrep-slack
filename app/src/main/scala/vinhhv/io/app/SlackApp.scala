package vinhhv.io.app

import vinhhv.io.Config.SitrepConfig
import zio.macros.accessible
import zio.{ Has, Task, URLayer, ZLayer }

@accessible
object SlackApp {
  type SlackApp = Has[Service]

  trait Service {
    def start: Task[Unit]
  }

  def live: URLayer[Has[SitrepConfig], SlackApp] =
    ZLayer.fromService(config => Live(config))
}
