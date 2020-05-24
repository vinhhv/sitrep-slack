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
      rts.unsafeRun {
        RIO.effectAsyncM[Clock, Unit] { _ =>
          client
            .setStatus(emoji, status)
            .delay(5.seconds)
            .foldM(
                err => client.sendMessage(s":cry: Something went wrong: ${err.getMessage}")
              , _ => ZIO.unit
            )
        } *> ZIO.succeed(ctx.ack(":joy: Scheduled!"))
      }
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
