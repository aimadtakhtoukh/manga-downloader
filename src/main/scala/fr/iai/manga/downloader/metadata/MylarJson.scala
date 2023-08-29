package fr.iai.manga.downloader.metadata

import fr.iai.manga.downloader.commons.MangaEntry
import fr.iai.manga.downloader.commons.TachideskApi.url
import zio.ZIO
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class MylarJson(metadata: Metadata)

object MylarJson {
  implicit val encoder: JsonEncoder[MylarJson] = DeriveJsonEncoder.gen[MylarJson]
}

case class Metadata(
                   `type`: String,
                   name: String,
                   description_formatted: String,
                   description_text: String,
                   status: String,
                   ComicImage: String,
                   publisher: String,
                   comicId: Long,
                   booktype: String
                   )

object Metadata {
  implicit val encoder: JsonEncoder[Metadata] = DeriveJsonEncoder.gen[Metadata]

  def apply(mangaEntry: MangaEntry): ZIO[Any, Throwable, Metadata] =
    for {
      u <- url
      metadata <- ZIO.succeed(
        Metadata(
          `type` = "comicSeries",
          name = mangaEntry.title,
          description_formatted = mangaEntry.description,
          description_text = mangaEntry.description,
          status = mangaEntry.status,
          ComicImage = s"$u${mangaEntry.thumbnailUrl}",
          publisher = mangaEntry.author.getOrElse(""),
          comicId = mangaEntry.id,
          booktype = "Print"
        )
      )
    } yield metadata
}


