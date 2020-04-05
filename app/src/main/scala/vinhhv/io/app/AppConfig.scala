package vinhhv.io.app

import com.typesafe.config.ConfigFactory
import vinhhv.io.Config.SitrepConfig
import zio.config.magnolia.ConfigDescriptorProvider
import zio.config.typesafe.TypesafeConfig
import zio.{ Has, Layer }

object AppConfig {
  type AppConfig = Has[SitrepConfig]

  def live: Layer[Throwable, AppConfig] =
    TypesafeConfig
      .fromHocon(ConfigFactory.load(), ConfigDescriptorProvider[SitrepConfig].getDescription("sitrep"))
      .map(config => Has(config.get.config))
}
