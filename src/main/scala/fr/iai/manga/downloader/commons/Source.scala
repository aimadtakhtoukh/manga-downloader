package fr.iai.manga.downloader.commons

import zio.json.{DeriveJsonDecoder, JsonDecoder}

case class Source(id: String, displayName: String)

object Source:
  implicit val decoder: JsonDecoder[Source] = DeriveJsonDecoder.gen[Source]
