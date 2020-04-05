package vinhhv.io

object Config {
  final case class SitrepConfig(slackAppConfig: SlackAppConfig)
  final case class SlackAppConfig(
        path: String
      , port: Int
      , slashCommand: String
      , botToken: String
      , clientId: String
      , clientSecret: String
      , signingSecret: String
  )
}
