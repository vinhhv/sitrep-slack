package vinhhv.io.config

import com.typesafe.config.ConfigFactory
import vinhhv.io.config.Config.SitrepConfig
import zio.{ Layer, config }
import zio.config.magnolia.ConfigDescriptorProvider
import zio.config.typesafe.TypesafeConfig

final case class Config(sitrep: SitrepConfig)

object Config {
  final case class SitrepConfig(slackAppConfig: SlackAppConfig)
  final case class SlackAppConfig(
        path: String
      , port: Int
      , slashCommand: String
      , clientId: String
      , clientSecret: String
      , signingSecret: String
  )
  def loadConfig: Layer[Throwable, config.Config[SitrepConfig]] =
    TypesafeConfig
      .fromHocon(ConfigFactory.load(), ConfigDescriptorProvider[SitrepConfig].getDescription("application.conf"))
}
