package vinhhv.io.client

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import vinhhv.io.Config.SitrepConfig
import zio.Task

private[client] final case class Live(sitrepConfig: SitrepConfig) extends SlackMethodsClient.Service {
  val methodsClient: MethodsClient = Slack.getInstance().methods(sitrepConfig.slackClientConfig.userToken)

  def setStatus(text: String, emoji: String): Task[Unit] = ???
}
