package vinhhv.io.app

import vinhhv.io.Config.SitrepConfig
import vinhhv.io.client.SlackMethodsClient
import vinhhv.io.client.SlackMethodsClient.SlackMethodsClient
import zio.macros.accessible
import zio.{ Has, RIO, URLayer, ZLayer }
import zio.clock.Clock
import zio.console.Console

@accessible
object SlackApp {
  type SlackApp = Has[Service]

  trait Service {
    def start: RIO[Clock with Console, Unit]
  }

  type LiveDeps = Has[SitrepConfig] with SlackMethodsClient
  def live: URLayer[LiveDeps, SlackApp] =
    ZLayer.fromServices[SitrepConfig, SlackMethodsClient.Service, SlackApp.Service] { (config, client) =>
      Live(config, client)
    }
}
