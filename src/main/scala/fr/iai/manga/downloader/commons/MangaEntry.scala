package fr.iai.manga.downloader.commons

import zio.json._

case class MangaEntry(
                       id: Long,
                       title: String,
                       artist: Option[String],
                       author: Option[String],
                       description: String,
                       genre: List[String],
                       status: String,
                       realUrl: String,
                       sourceId: String,
                       thumbnailUrl: String
                     )

object MangaEntry:
  implicit val decoder: JsonDecoder[MangaEntry] = DeriveJsonDecoder.gen[MangaEntry]
