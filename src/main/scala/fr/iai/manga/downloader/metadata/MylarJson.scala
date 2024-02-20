package fr.iai.manga.downloader.metadata

import fr.iai.manga.downloader.commons.MangaEntry
import fr.iai.manga.downloader.commons.TachideskApi.url
import zio.ZIO
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class MylarJson(metadata: Metadata)

object MylarJson:
  implicit val encoder: JsonEncoder[MylarJson] = DeriveJsonEncoder.gen[MylarJson]

case class Metadata(
                     `type`: String,
                     name: String,
                     description_formatted: String,
                     description_text: String,
                     status: String,
                     ComicImage: String,
                     publisher: String,
                     comicid: Long,
                     year: Int,
                     total_issues: Int,
                     booktype: String,
                     publication_run : String
                   )

object Metadata:
  implicit val encoder: JsonEncoder[Metadata] = DeriveJsonEncoder.gen[Metadata]

  private def mapStatus(status: String): String =
    status match {
      case "ONGOING" => "Continuing"
      case "FINISHED" => "Ended"
      case "COMPLETED" => "Ended"
      case _ => "Continuing"
    }

  def apply(mangaEntry: MangaEntry): ZIO[Any, Throwable, Metadata] =
    for {
      u <- url
      metadata <- ZIO.succeed(
        Metadata(
          `type` = "comicSeries",
          name = mangaEntry.title,
          description_formatted = mangaEntry.description,
          description_text = mangaEntry.description,
          status = mapStatus(mangaEntry.status),
          ComicImage = s"https://tachidesk.roleplay.ovh${mangaEntry.thumbnailUrl}",
          publisher = mangaEntry.author.getOrElse(""),
          comicid = 0,
          year = 0,
          total_issues = 0,
          booktype = "Print",
          publication_run = ""
        )
      )
    } yield metadata
