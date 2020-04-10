package vinhhv.io.client

import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.response.Response
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.emoji.EmojiListRequest
import com.slack.api.methods.request.users.profile.UsersProfileSetRequest
import com.slack.api.methods.response.emoji.EmojiListResponse
import com.slack.api.methods.response.users.profile.UsersProfileSetResponse
import com.slack.api.model.User
import vinhhv.io.client.Live._
import zio.{ Task, ZIO }

final case class SlackClientException(error: String) extends Exception(error)

private[client] final case class Live(
      methodsClient: MethodsClient
    , userToken: String
) extends SlackMethodsClient.Service {
  val textSuccess = "Status update was accepted! :middle_finger:"
  def textFailure(error: String, warning: String): String =
    s"""
       |Failed -
       |Error:   $error
       |Warning: $warning
       |""".stripMargin

  def verifyFormattedEmoji(emoji: Emoji): Task[Unit] = {
    val EmojiRegex = "^(:[\\w-]+:)$".r
    emoji match {
      case EmojiRegex(_) => ZIO.unit
      case _             => ZIO.fail(SlackClientException("Invalid formatted emoji"))
    }
  }

  def retrieveEmojis: Task[EmojiListResponse] = {
    val request = EmojiListRequest
      .builder()
      .token(userToken)
      .build()

    ZIO.effect(methodsClient.emojiList(request))
  }

//  def verifyEmojiExists(emojiStripped: EmojiStripped)(response: EmojiListResponse): Task[Unit] =
//    if (response.getEmoji.containsKey(emojiStripped)) ZIO.unit
//    else ZIO.fail(SlackClientException("Emoji is not valid"))
//
//  def handleResponse[R <: SlackApiResponse, A](
//        response: R
//      , handler: R => Task[A]
//  ): Task[Task[A]] =
//    if (response.isOk) ZIO.succeed(handler(response))
//    else ZIO.fail(SlackClientException(textFailure(response.getError, response.getWarning)))

  def setStatusM(emoji: Emoji, text: String): Task[Response] = {
    val profile = new User.Profile()
    profile.setStatusEmoji(emoji)
    profile.setStatusText(text)

    val request =
      UsersProfileSetRequest
        .builder
        .profile(profile)
        .build

    ZIO
      .effect(methodsClient.usersProfileSet(request))
      .flatMap { response: UsersProfileSetResponse =>
        if (response.isOk) ZIO.succeed(createResponse(textSuccess))
        else ZIO.fail(SlackClientException(textFailure(response.getError, response.getWarning)))
      }
  }

  def setStatus(emoji: Emoji, text: String): Task[Response] =
    for {
      _        <- verifyFormattedEmoji(emoji)
      response <- setStatusM(emoji, text)
    } yield response
}

private[client] object Live {
  type Emoji = String

  val StatusCodeSuccess = 200

  def createResponse(text: String): Response =
    Response.json(StatusCodeSuccess, SlashCommandResponse.builder.text(text).build)
}
