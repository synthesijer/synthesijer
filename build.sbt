lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "synthesijer",
      scalaVersion := "2.13.14",
      version      := "3.1.0",
      javacOptions ++= Seq("-source", "17", "-target", "17"),
      javacOptions += "-Xlint:unchecked",
      javacOptions += "-Xlint:deprecation",
      crossPaths := false,
    )),
    name := "synthesijer",
    autoScalaLibrary := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.5" % "test",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      "junit" % "junit" % "4.13.2" % "test",
      "com.novocode" % "junit-interface" % "0.11" % "test"
    )
  )

mainClass in (Compile, packageBin) := Some("synthesijer.Main")
mainClass in assembly := Some("synthesijer.Main")

import sbtassembly.AssemblyPlugin.defaultUniversalScript
//assemblyOption in assembly :=
  //(assemblyOption in assembly)
    //.value
    //.copy(prependShellScript = Some(
      //Seq(scala.io.Source.fromFile("scripts/run.script", "UTF-8").mkString)))
assemblyJarName in assembly := s"${name.value}"

publishTo := Some(Resolver.file("miyo", file("pub"))(Patterns(true, Resolver.mavenStyleBasePattern)))
