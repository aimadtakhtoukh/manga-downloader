package fr.iai.manga.downloader.scripts

import fr.iai.manga.downloader.commons.TachideskApi.*
import io.netty.util.CharsetUtil
import zio.*
import zio.http.Client

object Download extends ZIOAppDefault :

  private val program: ZIO[Client, Throwable, Unit] = for {
    mangaList <- mangaList()
    mangaId <- ZIO.succeed(mangaList.head.id)
//    chapterList <- chapterList(mangaId)
//    chapterId <- ZIO.succeed(chapterList.head.id)
    api <- apiUrl
    chapterListReq <- Client.request(s"$api/manga/$mangaId/chapters")
    chapterListAsString <- chapterListReq.body.asString(CharsetUtil.UTF_8)
//    result <- Client.request(s"$apiUrl/download/$mangaId/chapter/$chapterId")
//    downloaded <- isDownloaded(mangaId, chapterId) repeat (Schedule.spaced(100.millisecond) && Schedule.recurUntilEquals(true))
  } yield ()

  override val run: ZIO[Any, Throwable, Unit] = program.provide(Client.default)