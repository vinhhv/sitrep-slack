package vinhhv.io.client

import com.slack.api.Slack
import com.slack.api.bolt.response.Response
import vinhhv.io.Config.SitrepConfig
import zio.macros.accessible
import zio.{ Has, Task, URLayer, ZLayer }

@accessible
object SlackMethodsClient {
  type SlackMethodsClient = Has[Service]

  trait Service {
    def setStatus(text: String, emoji: String): Task[Response]
  }

  def live: URLayer[Has[SitrepConfig], SlackMethodsClient] =
    ZLayer.fromService[SitrepConfig, Service] { config =>
      Live(Slack.getInstance.methods(config.slackAppConfig.userToken))
    }
}
