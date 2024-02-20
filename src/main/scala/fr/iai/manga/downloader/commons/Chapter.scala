package fr.iai.manga.downloader.commons

import zio.json._

case class Chapter(
                    id: Long,
                    name: String,
                    chapterNumber: Double,
                    realUrl: String,
                    pageCount: Int,
                    chapterCount: Int,
                    index: Int,
                    downloaded: Boolean,
                    scanlator: Option[String]
                  )

object Chapter:
  implicit val decoder: JsonDecoder[Chapter] = DeriveJsonDecoder.gen[Chapter]
