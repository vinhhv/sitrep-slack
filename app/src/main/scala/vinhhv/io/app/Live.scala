package vinhhv.io.app

import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.bolt.{ App, AppConfig }
import vinhhv.io.Config.SitrepConfig
import vinhhv.io.client.SlackMethodsClient
import zio.{ Task, ZIO }

private[app] final case class Live(
      sitrepConfig: SitrepConfig
    , client: SlackMethodsClient.Service
) extends SlackApp.Service {
  val config = sitrepConfig.slackAppConfig
  val appConfig: AppConfig =
    AppConfig
      .builder()
      .singleTeamBotToken(config.botToken)
      .signingSecret(config.signingSecret)
      .build()
  val handler: SlashCommandHandler = (_, ctx) => ctx.ack(":wave: Hello!")
  val slackApp: App                = new App().command(config.slashCommand, handler)

  def start: Task[Unit] =
    ZIO.effect(new SlackAppServer(slackApp, config.path, config.port).start())
}
