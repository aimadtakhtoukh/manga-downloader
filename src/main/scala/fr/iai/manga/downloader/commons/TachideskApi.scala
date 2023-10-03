package fr.iai.manga.downloader.commons

import zio.*
import zio.http.Client
import io.netty.util.CharsetUtil
import zio.json.*

object TachideskApi:

   def url: IO[Throwable, String] = System.env("TACHIDESK_URL").flatMap {
      case Some(url) => ZIO.succeed(url)
      case None => ZIO.fail(new IllegalStateException("Undefined URL"))
   }

   def apiUrl: IO[Throwable, String] = url.map(u => s"$u/api/v1")

   def mangaList(): ZIO[Client, Throwable, List[MangaEntry]] =
      for {
         api <- apiUrl
         mangaListReq <- Client.request(s"$api/category/0")
         mangaListAsString <- mangaListReq.body.asString(CharsetUtil.UTF_8)
         mangaList <- ZIO.fromEither(mangaListAsString.fromJson[List[MangaEntry]]).mapError(errorMessage => IllegalStateException(errorMessage))
      } yield mangaList

   def chapterList(id: Long): ZIO[Client, Throwable, List[Chapter]] =
      for {
         api <- apiUrl
         chapterListReq <- Client.request(s"$api/manga/$id/chapters")
         chapterListAsString <- chapterListReq.body.asString(CharsetUtil.UTF_8)
         chapterList <- ZIO.fromEither(chapterListAsString.fromJson[List[Chapter]]).mapError(errorMessage => IllegalStateException(errorMessage))
      } yield chapterList

   def updateChapterList(id: Long): ZIO[Client, Throwable, Unit] =
      for {
         api <- apiUrl
         _ <- Client.request(s"$api/manga/$id/?onlineFetch=true")
      } yield ()

   def isDownloaded(mangaId: Long, chapterId: Long): ZIO[Client, Throwable, Boolean] =
      for {
         api <- apiUrl
         chapterInfoReq <- Client.request(s"$api/manga/$mangaId/chapter/$chapterId")
         chapterInfoAsString <- chapterInfoReq.body.asString(CharsetUtil.UTF_8)
         chapterInfo <- ZIO.fromEither(chapterInfoAsString.fromJson[Chapter]).mapError(errorMessage => IllegalStateException(errorMessage))
      } yield chapterInfo.downloaded

   def source(sourceId: String): ZIO[Client, Throwable, Source] =
      for {
         api <- apiUrl
         sourceReq <- Client.request(s"$api/source/$sourceId")
         sourceReqAsString <- sourceReq.body.asString(CharsetUtil.UTF_8)
         source <- ZIO.fromEither(sourceReqAsString.fromJson[Source]).mapError(errorMessage => IllegalStateException(errorMessage))
      } yield source
