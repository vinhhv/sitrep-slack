package vinhhv.io

object Config {
  final case class SitrepConfig(slackAppConfig: SlackAppConfig, slackClientConfig: SlackClientConfig)
  final case class SlackAppConfig(
        path: String
      , port: Int
      , slashCommand: String
      , botToken: String
      , clientId: String
      , clientSecret: String
      , signingSecret: String
  )
  final case class SlackClientConfig(userToken: String)
}
