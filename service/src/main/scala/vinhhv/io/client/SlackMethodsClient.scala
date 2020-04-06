package vinhhv.io.client

import zio.macros.accessible
import zio.{ Has, Task }

@accessible
object SlackMethodsClient {
  type SlackMethodsClient = Has[Service]

  trait Service {
    def setStatus(text: String, emoji: String): Task[Unit]
  }
}
