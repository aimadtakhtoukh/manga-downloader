package fr.iai.manga.downloader.scripts

import zio.http.Client
import zio.stream.ZSink
import zio.{ZIO, ZIOAppDefault}

import java.nio.file.Paths

object ThumbnailDownload extends ZIOAppDefault :
  private val program: ZIO[Client, Throwable, Unit] =
    for {
      result <- Client.request("http://localhost:4567/api/v1/manga/25/thumbnail")
      body <- ZIO.succeed(result.body.asStream)
      _ <- body.run(
        ZSink.fromFile(
          Paths.get("thumbnail.png").toFile
        )
      )
    } yield ()

  override val run: ZIO[Any, Throwable, Unit] = program.provide(Client.default)