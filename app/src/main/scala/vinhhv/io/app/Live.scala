package vinhhv.io.app

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.{ App, AppConfig }
import vinhhv.io.Config.SitrepConfig
import vinhhv.io.client.SlackMethodsClient
import zio.{ RIO, ZIO }
import zio.clock.Clock
import zio.duration._

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

  val handler: RIO[Clock, SlashCommandHandler] = ZIO.runtime.map {
    rts => (req: SlashCommandRequest, ctx: SlashCommandContext) =>
      val (emoji, status) = Live.parse(req.getPayload.getText)
      rts.unsafeRunAsync(
          client
          .setStatus(emoji, status)
          .delay(5.seconds)
          .foldM(
              err => ZIO.succeed(ctx.ack(s":cry: Something went wrong: ${err.getMessage}"))
            , res => ZIO.succeed(res)
          )
      )(_ => ())
      rts.unsafeRun(ZIO.succeed(ctx.ack(s":joy: Scheduled!")))
  }

  def start: RIO[Clock, Unit] =
    for {
      handler <- handler
      app = new App(appConfig).command(config.slashCommand, handler)
    } yield new SlackAppServer(app, config.path, config.port).start()
}

private object Live {
  def parse(text: String): (String, String) =
    text.trim.split(" ", 2).toList match {
      case emoji :: status :: _ => (emoji, status)
      case _                    => (":middle_finger:", "This is the default status")
    }
}
