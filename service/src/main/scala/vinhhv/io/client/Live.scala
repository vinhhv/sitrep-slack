package vinhhv.io.client

import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.response.Response
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.users.profile.UsersProfileSetRequest
import com.slack.api.model.User
import zio.{ Task, ZIO }

private[client] final case class Live(methodsClient: MethodsClient) extends SlackMethodsClient.Service {
  val statusCodeSuccess = 200
  val statusCodeFailure = 406

  val textSuccess = "Status update was accepted! :middle_finger:"
  def textFailure(error: String, warning: String): String =
    s"""
       |Failed -
       |Error:   $error
       |Warning: $warning
       |""".stripMargin

  def setStatus(emoji: String, text: String): Task[Response] = {
    val profile = new User.Profile()
    profile.setStatusEmoji(emoji)
    profile.setStatusText(text)

    val profileSetRequest =
      UsersProfileSetRequest
        .builder
        .profile(profile)
        .build

    ZIO
      .effect(methodsClient.usersProfileSet(profileSetRequest))
      .map { setResponse =>
        if (setResponse.isOk) {
          val text = textSuccess
          Response.json(statusCodeSuccess, SlashCommandResponse.builder.text(text).build)
        } else {
          val text = textFailure(setResponse.getError, setResponse.getWarning)
          Response.json(statusCodeFailure, SlashCommandResponse.builder.text(text).build)
        }
      }
  }
}
