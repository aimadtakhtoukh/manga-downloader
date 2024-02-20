package fr.iai.manga.downloader.graphql

import Client.*
import caliban.client.CalibanClientError
import fr.iai.manga.downloader.graphql.Client.Category._
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3.*
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio.Console.printLine
import zio.*

object GraphQLExample extends ZIOAppDefault :

  case class CategoryView(id: Int, name: String, order: Int)

  def run = {
//    val category = {
//      import Client.Category._
//      (id ~ name ~ order)
//        .mapN(CategoryView)
//    }
    val query =
      Queries.category(0) {
        (id ~ name ~ order).mapN(CategoryView)
      }

    def sendRequest[T](
                        req: Request[Either[CalibanClientError, T], Any]
                      ): RIO[SttpBackend[Task, ZioStreams with WebSockets], T] =
      ZIO
        .serviceWithZIO[SttpBackend[Task, ZioStreams with WebSockets]](req.send(_))
        .map(_.body)
        .absolve
        .tap(res => printLine(s"Result: $res"))

    val uri = uri"http://localhost:4567/api/graphql"
    val call = sendRequest(query.toRequest(uri, useVariables = true))

    call.provideLayer(HttpClientZioBackend.layer())
  }

