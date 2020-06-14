package vinhhv.io.app

import com.slack.api.bolt.{ App, AppConfig }
import com.slack.api.bolt.context.builtin.{ GlobalShortcutContext, SlashCommandContext }
import com.slack.api.bolt.handler.builtin.{ GlobalShortcutHandler, SlashCommandHandler }
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.bolt.request.builtin.{ GlobalShortcutRequest, SlashCommandRequest }
import com.slack.api.bolt.response.Response
import com.slack.api.methods.request.views.ViewsOpenRequest
import vinhhv.io.Config.SitrepConfig
import vinhhv.io.client.SlackMethodsClient
import vinhhv.io.modals.SitrepModal
import zio.{ RIO, Task, ZIO, console => ZConsole }
import zio.clock.Clock
import zio.console.Console
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

  private def sendMessage(message: String, channelId: String) =
    client
      .sendMessage(message, channelId)
      .foldM(
          err => ZConsole.putStrLn(err.getMessage)
        , _ => ZIO.unit
      )

  val slashCommandHandler: RIO[Clock with Console, SlashCommandHandler] = ZIO.runtime.map {
    rts => (req: SlashCommandRequest, ctx: SlashCommandContext) =>
      val (emoji, status) = Live.parse(req.getPayload.getText)
//      val scheduledTimestamp = ???
      val userId = req.getPayload.getUserId
      rts.unsafeRun {
        client
          .setStatus(emoji, status)
          .delay(5.seconds)
          .foldM(
              err => sendMessage(s""":cry: Could not set your status: "$status"""", userId) *> ZIO.fail(err)
            , _ => sendMessage(s""":raised_hands: Successfully set your status: "$status"!""", userId)
          )
          .forkDaemon *> ZIO.succeed(ctx.ack(":alarm_clock: Scheduled!"))
      }
  }

  val shortcutHandler: Task[GlobalShortcutHandler] = ZIO.runtime.map {
    rts => (_: GlobalShortcutRequest, ctx: GlobalShortcutContext) =>
      rts.unsafeRun {
        ZIO
          .effect {
            ctx
              .client
              .viewsOpen(
                  ViewsOpenRequest
                  .builder()
                  .triggerId(ctx.getTriggerId)
                  .view(SitrepModal.buildView(config.shortcutCallbackId))
                  .build
              )
          }
          .map { viewResponse =>
            if (viewResponse.isOk) ctx.ack
            else Response.builder.statusCode(500).body(viewResponse.getError).build
          }
      }
  }

  def start: RIO[Clock with Console, Unit] =
    for {
      slashCommandHandler <- slashCommandHandler
      shortcutHanlder     <- shortcutHandler
      app = new App(appConfig)
        .command(config.slashCommand, slashCommandHandler)
        .globalShortcut(config.shortcutCallbackId, shortcutHanlder)
    } yield new SlackAppServer(app, config.path, config.port).start()
}

private object Live {
  def parse(text: String): (String, String) =
    text.trim.split(" ", 2).toList match {
      case emoji :: status :: _ => (emoji, status)
      case _                    => (":middle_finger:", "This is the default status")
    }
}
