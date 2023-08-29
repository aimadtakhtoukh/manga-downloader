package fr.iai.manga.downloader.scripts

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import fr.iai.manga.downloader.commons.TachideskApi.*
import fr.iai.manga.downloader.commons.{Chapter, MangaEntry}
import fr.iai.manga.downloader.metadata.ComicInfo
import zio.http.Client
import zio.{ZIOAppDefault, *}

import java.io.StringWriter

object MetadataGeneration extends ZIOAppDefault :

  private val mapper: XmlMapper = XmlMapper()
  mapper.enable(SerializationFeature.INDENT_OUTPUT);
  mapper.registerModule(DefaultScalaModule)

  private def convert(comicInfo: ComicInfo): String =
    val out = new StringWriter
    mapper.writeValue(out, comicInfo)
    out.toString

  def generateMetadata(mangaEntry: MangaEntry, chapter: Chapter) : ZIO[Any, Nothing, String] =
    for {
      comicInfo <- ZIO.succeed(ComicInfo(mangaEntry, chapter))
      res <- ZIO.succeed(convert(comicInfo))
    } yield res

  private val program: ZIO[Client, Throwable, Unit] = for {
    mangaList <- mangaList()
    manga <- ZIO.succeed(mangaList.head)
    chapterList <- chapterList(manga.id)
    comicInfoList <- ZIO.foreachPar(chapterList)(chapter => ZIO.succeed(ComicInfo(manga, chapter)))
    _ <- Console.printLine(comicInfoList)
    _ <- Console.printLine(convert(comicInfoList.head))
  } yield ()

  override val run: ZIO[Any, Throwable, Unit] = program.provide(Client.default)
