lazy val root = project
  .in(file("."))
  .settings(
    name := "manga-downloader",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := "3.3.0",

    libraryDependencies ++= List(
      "dev.zio" %% "zio" % "2.0.15",
      "dev.zio" %% "zio-http" % "3.0.0-RC2"
    )
  )
