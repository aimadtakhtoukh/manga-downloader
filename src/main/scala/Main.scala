import zio.{Console, ZIO, ZIOAppDefault}
import zio.http.Client

object Main extends ZIOAppDefault :
  private def downloadUrl = "http://localhost:4567/api/v1/download/101/chapter/105"
  private def downloadTrackingWS = "ws://localhost:4567/api/v1/downloads"

  private val program: ZIO[Client, Throwable, Unit] = for {
    res  <- Client.request(downloadUrl)
    data <- ZIO.succeed(res.status)
    _    <- Console.printLine(data)
  } yield ()

  override val run = program.provide(Client.default)