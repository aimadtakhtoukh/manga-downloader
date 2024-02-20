package fr.iai.manga.downloader.metadata

import fr.iai.manga.downloader.commons.{Chapter, MangaEntry}

case class ComicInfo(
                      title: String,
                      series: String,
                      number: Double,
                      web: String,
                      genre: String,
                      pageCount: Int,
                      summary: String,
                      writer: String,
                      tags: String,
                      manga: String
                    )

object ComicInfo:
  def apply(mangaEntry: MangaEntry, chapter: Chapter) : ComicInfo = ComicInfo(
    title = chapter.name,
    series = mangaEntry.title,
    number = chapter.chapterNumber,
    web = chapter.realUrl,
    genre = mangaEntry.genre.mkString(", "),
    pageCount = chapter.pageCount,
    summary = mangaEntry.description,
    writer = (mangaEntry.artist :: mangaEntry.author :: Nil).collect({case Some(a) => a}).mkString(", "),
    tags = "",
    manga = "YesAndRightToLeft"
  )
