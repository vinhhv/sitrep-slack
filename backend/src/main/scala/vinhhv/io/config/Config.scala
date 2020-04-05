package vinhhv.io.config

import com.typesafe.config.ConfigFactory
import zio.{ Has, Layer, config }
import zio.config.magnolia.ConfigDescriptorProvider
import zio.config.typesafe.TypesafeConfig

object Config {
  type Config = Has[config.Config[SitrepConfig]]

  final case class BaseConfig(sitrep: SitrepConfig)
  final case class SitrepConfig(slackAppConfig: SlackAppConfig)
  final case class SlackAppConfig(
        path: String
      , port: Int
      , slashCommand: String
      , clientId: String
      , clientSecret: String
      , signingSecret: String
  )

  def live: Layer[Throwable, Has[config.Config[SitrepConfig]]] =
    TypesafeConfig
      .fromHocon(ConfigFactory.load(), ConfigDescriptorProvider[SitrepConfig].getDescription("application.conf"))
      .map(Has(_))
}
