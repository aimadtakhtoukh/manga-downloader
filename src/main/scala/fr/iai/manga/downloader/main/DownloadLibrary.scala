package fr.iai.manga.downloader.main

import fr.iai.manga.downloader.commons.TachideskApi.{apiUrl, isDownloaded, url}
import fr.iai.manga.downloader.commons.{Chapter, MangaEntry, TachideskApi}
import fr.iai.manga.downloader.metadata.*
import fr.iai.manga.downloader.scripts.MetadataGeneration
import fr.iai.manga.downloader.scripts.Zip.*
import zio.*
import zio.http.*
import zio.json.*
import zio.nio.file.Files
import zio.stream.ZSink

object DownloadLibrary extends ZIOAppDefault :
  private def ifDownloaded(mangaEntry: MangaEntry, chapter: Chapter): ZIO[Client, Throwable, Boolean] =
    isDownloaded(mangaEntry.id, chapter.index) repeat (Schedule.spaced(1.second) && Schedule.recurUntilEquals(true))
      .map { case (_, downloaded) => downloaded }

  private def handleChapter(entry: MangaEntry, chapter: Chapter): URIO[Client, Unit] =
    (
      for {
        // Check if chapter downloaded
        chapterExists <- chapterAlreadyDownloaded(entry.title, chapter.name)
        _ <-
          if (chapterExists) {
            ZIO.fail(new IllegalStateException("Chapter already exists"))
          } else {
            Console.printLine(s"${entry.title}, chp ${chapter.chapterNumber} started") *>
              ZIO.succeed(())
          }
        // Trigger download
        api <- apiUrl
        downloaded <- ifDownloaded(entry, chapter)
        downloadUrl = s"$api/download/${entry.id}/chapter/${chapter.index}"
        _ <- ZIO.when(!downloaded)(Client.request(downloadUrl))
        // Zip manga
        source <- TachideskApi.source(entry.sourceId)
        _ <- ZIO.succeed(downloaded) *> zipManga(source.displayName, entry.title, chapter.scanlator, chapter.name)
        // Generate metadata
        comicInfo <- MetadataGeneration.generateMetadata(entry, chapter)
        done <- ZIO.succeed(downloaded) *> addComicInfo(entry.title, chapter.name, comicInfo)
        _ <- ZIO.succeed(done) *> Console.printLine(s"${entry.title}, chp ${chapter.chapterNumber} ended")
      } yield ()
    ).catchAll(t => ZIO.succeed(t.printStackTrace()) *> ZIO.succeed(()))

  private def downloadPoster(entry: MangaEntry) =
    for {
      u <- url
      result <- Client.request(s"$u${entry.thumbnailUrl}")
      contentType <- ZIO.fromOption(result.header(Header.ContentType)).orElseFail(IllegalStateException())
      mediaType <- ZIO.succeed(contentType.mediaType).map(_.subType)
      mangaFolder <- destinationPath.map(_ / clean(entry.title))
      _ <- Files.createDirectories(mangaFolder).orDie
      posterName = s"poster.$mediaType"
      body = result.body.asStream
      posterSink = ZSink.fromFile((mangaFolder / posterName).toFile)
      _ <- body.run(posterSink)
    } yield ()

  private def setUpMylarJson(entry: MangaEntry) =
    for {
      metadata <- Metadata(entry)
      mylarJson = MylarJson(metadata = metadata)
      jsonContent = mylarJson.toJsonPretty
      mangaFolder <- destinationPath.map(_ / clean(entry.title))
      _ <- Files.createDirectories(mangaFolder).orDie
      mylarJsonFile = mangaFolder / "series.json"
      _ = mylarJsonFile.toFile.createNewFile()
      _ <- Files.writeLines(mylarJsonFile, jsonContent :: Nil)
    } yield ()

  private def handleManga(entry: MangaEntry) =
    for {
      _ <- Console.printLine(s"${entry.title} started")
      // Mylar json
      _ <- setUpMylarJson(entry)
      // Thumbnail
      _ <- downloadPoster(entry)
      // Update chapter list
      _ <- TachideskApi.updateChapterList(entry.id)
      // Download chapters if not already downloaded
      chapterList <- TachideskApi.chapterList(entry.id)
      done <- ZIO.foreachParDiscard(chapterList)(chapter => handleChapter(entry, chapter))
      _ <- ZIO.succeed(done) *> Console.printLine(s"${entry.title} ended")
    } yield ()

  private val program: ZIO[Client, Throwable, Unit] = for {
    mangaList <- TachideskApi.mangaList()
    _ <- ZIO.foreachParDiscard(mangaList)(handleManga)
  } yield ()

  override val run: ZIO[Any, Throwable, Unit] =
    program.provide(Client.default)
      .repeat(Schedule.spaced(2.hours))
      .forever
