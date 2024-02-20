enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(CalibanPlugin)

lazy val root = project
  .in(file("."))
  .settings(
    name := "manga-downloader",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := "3.3.0",

    libraryDependencies ++= List(
      "org.slf4j" % "slf4j-nop" % "2.0.7",
      "dev.zio" %% "zio" % "2.0.16",
      "dev.zio" %% "zio-http" % "3.0.0-RC2",
      "com.softwaremill.sttp.client3" %% "zio" % "3.9.3",

      //GraphQL client
      "com.github.ghostdogpr" %% "caliban-client" % "2.5.2",

      //Streams
      "dev.zio" %% "zio-streams" % "2.0.16",

      // Gestion des ZIP
      "dev.zio" %% "zio-nio" % "2.0.1",
      "org.zeroturnaround" % "zt-zip" % "1.16",

      // Gestion du xml
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.15.0",
      "org.codehaus.woodstox" % "woodstox-core-asl" % "4.4.1"
    )
  )

dockerEntrypoint := Seq("/opt/docker/bin/download-library")
