package fr.iai.manga.downloader.scripts

import org.zeroturnaround.zip.ZipUtil
import zio.*
import zio.nio.file.*

object Zip extends ZIOAppDefault :
  private val source: String = "Asura Scans (EN)"
  val manga: String = "The Greatest Estate Developer"
  val scanlator: Option[String] = None
  val chapter: String = "Chapter 104.5"

  def clean(fileName: String): String = fileName.replaceAll("[:\"?]", "_").replaceAll("\\.*$", "").take(240)

  def sourcePath: IO[Throwable, Path] = System.env("SOURCE_PATH").flatMap({
    case Some(path) => ZIO.succeed(Path(path))
    case None => ZIO.fail(IllegalStateException("Source Path undefined"))
  })

  def destinationPath: IO[Throwable, Path] = System.env("DESTINATION_PATH").flatMap({
    case Some(path) => ZIO.succeed(Path(path))
    case None => ZIO.fail(IllegalStateException("Destination Path undefined"))
  })

  private def mangaImageSource(source: String)(manga: String)(scanlatorOption: Option[String], chapter: String): IO[Throwable, Path] =
    scanlatorOption match {
      case Some(scanlator) => sourcePath.map(_ / source / manga / s"${scanlator}_$chapter")
      case None => sourcePath.map(_ / source / manga / chapter)
    }


  private def mangaZipDestination(manga: String)(chapter: String): IO[Throwable, Path] =
    for {
      mangaComicsFolder <- destinationPath.map(_ / manga)
      _ <- Files.createDirectories(mangaComicsFolder).orDie
      path <- ZIO.succeed(mangaComicsFolder / s"$chapter.cbz")
    } yield path

  def chapterAlreadyDownloaded(manga: String, chapter: String): IO[Throwable, Boolean] =
    for {
      cleanedManga <- ZIO.succeed(clean(manga))
      cleanedChapter <- ZIO.succeed(clean(chapter))
      destinationFile <- mangaZipDestination(cleanedManga)(cleanedChapter)
      exists <- Files.exists(destinationFile)
    } yield exists

  def zipManga(source: String, manga: String, scanlator: Option[String], chapter: String): IO[Throwable, Unit] =
    for {
      cleanedManga <- ZIO.succeed(clean(manga))
      cleanedChapter <- ZIO.succeed(clean(chapter))
      mangaFolder <- mangaImageSource(source)(cleanedManga)(scanlator, cleanedChapter).map(_.toFile)
      destinationFile <- mangaZipDestination(cleanedManga)(cleanedChapter).map(_.toFile)
      _ <- ZIO.succeed(ZipUtil.pack(mangaFolder, destinationFile))
    } yield ()

  def addComicInfo(manga: String, chapter: String, comicInfo: String): IO[Throwable, Unit] =
    for {
      cleanedManga <- ZIO.succeed(clean(manga))
      cleanedChapter <- ZIO.succeed(clean(chapter))
      baseFile <- mangaZipDestination(cleanedManga)(cleanedChapter).map(_.toFile)
      _ <- ZIO.succeed(ZipUtil.addEntry(baseFile, "ComicInfo.xml", comicInfo.getBytes()))
    } yield ()

  private val program: ZIO[Any, Throwable, Unit] = zipManga(source, manga, scanlator, chapter)
  override val run: ZIO[Any, Throwable, Unit] = program







