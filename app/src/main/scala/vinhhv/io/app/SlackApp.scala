package vinhhv.io.app

import vinhhv.io.app.AppConfig.AppConfig
import zio.macros.accessible
import zio.{ Has, Task, URLayer, ZLayer }

@accessible
object SlackApp {
  type SlackApp = Has[Service]

  trait Service {
    def start: Task[Unit]
  }

  def live: URLayer[AppConfig, SlackApp] =
    ZLayer.fromService(config => Live(config))
}
