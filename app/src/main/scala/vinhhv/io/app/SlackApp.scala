package vinhhv.io.app

import vinhhv.io.Config.SitrepConfig
import vinhhv.io.client.SlackMethodsClient
import vinhhv.io.client.SlackMethodsClient.SlackMethodsClient
import zio.macros.accessible
import zio.{ Has, Task, URLayer, ZLayer }

@accessible
object SlackApp {
  type SlackApp = Has[Service]

  trait Service {
    def start: Task[Unit]
  }

  type LiveDeps = Has[SitrepConfig] with SlackMethodsClient
  def live: URLayer[LiveDeps, SlackApp] =
    ZLayer.fromServices[SitrepConfig, SlackMethodsClient.Service, SlackApp.Service] { (config, client) =>
      Live(config, client)
    }
}
