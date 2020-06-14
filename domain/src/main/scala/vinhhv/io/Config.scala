package vinhhv.io

object Config {
  final case class SitrepConfig(slackAppConfig: SlackAppConfig)
  final case class SlackAppConfig(
        path: String
      , port: Int
      , slashCommand: String
      , shortcutCallbackId: String
      , botToken: String
      , userToken: String
      , clientId: String
      , clientSecret: String
      , signingSecret: String
  )
}
