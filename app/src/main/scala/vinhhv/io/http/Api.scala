package vinhhv.io.http

import io.circe.{ Decoder, Encoder }
import org.http4s.{ EntityDecoder, EntityEncoder, HttpRoutes }
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import zio.RIO
import zio.interop.catz._

final case class Api[R]() {
  type HttpTask[A] = RIO[R, A]

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[HttpTask, A] =
    jsonOf[HttpTask, A]
  implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]): EntityEncoder[HttpTask, A] =
    jsonEncoderOf[HttpTask, A]

  val dsl: Http4sDsl[HttpTask] = Http4sDsl[HttpTask]
  import dsl._

  def routes: HttpRoutes[HttpTask] =
    HttpRoutes.of[HttpTask] {
      case GET -> Root => Ok()
    }
}
