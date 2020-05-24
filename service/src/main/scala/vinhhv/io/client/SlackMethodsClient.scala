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
    def setStatus(emoji: String, status: String): Task[Response]
    def sendMessage(message: String): Task[Unit]
  }

  def live: URLayer[Has[SitrepConfig], SlackMethodsClient] =
    ZLayer.fromService[SitrepConfig, Service] { config =>
      val userToken     = config.slackAppConfig.userToken
      val methodsClient = Slack.getInstance.methods(userToken)
      Live(methodsClient, userToken)
    }
}
