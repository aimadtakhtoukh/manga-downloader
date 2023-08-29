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

      "dev.zio" %% "zio-config" % "3.0.7",
      "dev.zio" %% "zio-config-magnolia" % "3.0.7",
      "dev.zio" %% "zio-config-typesafe" % "3.0.7",
      "dev.zio" %% "zio-config-refined" % "3.0.7",

      //Streams
      "dev.zio" %% "zio-core" % "2.0.16" % Test,
      "dev.zio" %% "zio-streams" % "2.0.16" % Test,

      // Gestion des ZIP
      "dev.zio" %% "zio-nio" % "2.0.1",
      "org.zeroturnaround" % "zt-zip" % "1.16",

      // Gestion du xml
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.15.0",
      "org.codehaus.woodstox" % "woodstox-core-asl" % "4.4.1"
    )
  )
