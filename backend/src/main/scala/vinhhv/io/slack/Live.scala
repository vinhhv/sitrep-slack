package vinhhv.io.slack

import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.bolt.{ App, AppConfig }
import vinhhv.io.config.Config.SitrepConfig
import zio.{ Task, ZIO }
import zio.config.Config

private[slack] final case class Live(sitrepConfig: Config[SitrepConfig]) extends SlackApp.Service {
  val config = sitrepConfig.get.config.slackAppConfig
  val appConfig: AppConfig =
    AppConfig
      .builder()
      .clientId(config.clientId)
      .clientSecret(config.clientSecret)
      .signingSecret(config.signingSecret)
      .build()
  val handler: SlashCommandHandler = (_, ctx) => ctx.ack(":wave: Hello!")
  val slackApp: App                = new App(appConfig).command(config.slashCommand, handler)

  def start(path: String, port: Int): Task[Unit] =
    ZIO.effect(new SlackAppServer(slackApp, path, port).start())
}
