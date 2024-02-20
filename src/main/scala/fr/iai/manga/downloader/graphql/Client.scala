package fr.iai.manga.downloader.graphql

import caliban.client.FieldBuilder.{ListOf, Obj, Scalar}
import caliban.client.Operations.RootQuery
import caliban.client.{Argument, SelectionBuilder}
import caliban.client.SelectionBuilder.Field

object Client:

  type Category
  object Category:
    def id: SelectionBuilder[Category, Int] = Field("id", Scalar())
    def name: SelectionBuilder[Category, String] = Field("name", Scalar())
    def order: SelectionBuilder[Category, Int] = Field("order", Scalar())

  type Queries = RootQuery
  object Queries:
    def category[A](id: Int)(innerSelection: SelectionBuilder[Category, A]) : SelectionBuilder[RootQuery, List[A]] =
      Field("category", ListOf(Obj(innerSelection)), arguments = List(Argument("id", id, "Int!")))
