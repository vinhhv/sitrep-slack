package vinhhv.io.slack

import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.{ App, AppConfig }
import com.slack.api.bolt.jetty.SlackAppServer
import vinhhv.io.config.Config.SlackAppConfig
import zio.{ Task, ZIO }

final case class SlackApp(slackAppConfig: SlackAppConfig) {
  val appConfig: AppConfig =
    AppConfig
      .builder()
      .clientId(slackAppConfig.clientId)
      .clientSecret(slackAppConfig.clientSecret)
      .signingSecret(slackAppConfig.signingSecret)
      .build()
  val handler: SlashCommandHandler = (_, ctx) => ctx.ack(":wave: Hello!")
  val slackApp: App                = new App(appConfig).command(slackAppConfig.slashCommand, handler)

  def start(path: String, port: Int): Task[Unit] =
    ZIO.effect(new SlackAppServer(slackApp, path, port).start())
}
