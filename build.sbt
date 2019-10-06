lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "synthesijer",
      scalaVersion := "2.12.1",
      version      := "3.0.1",
      javacOptions ++= Seq("-source", "11", "-target", "11"),
      crossPaths := false,
    )),
    name := "synthesijer",
    autoScalaLibrary := false,
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.12" % "test",
      "com.novocode" % "junit-interface" % "0.11" % "test"
    )
  )

mainClass in (Compile, packageBin) := Some("synthesijer.Main")
mainClass in assembly := Some("synthesijer.Main")
assemblyJarName in assembly := "synthesijer.jar"

import sbtassembly.AssemblyPlugin.defaultUniversalScript
//assemblyOption in assembly := 
//  (assemblyOption in assembly).value.copy(prependShellScript =
//                         Some(defaultUniversalScript(shebang = false)))
assemblyOption in assembly :=
  (assemblyOption in assembly)
    .value
    .copy(prependShellScript = Some(
      Seq(scala.io.Source.fromFile("scripts/run.script", "UTF-8").mkString)))
//assemblyJarName in assembly := s"${name.value}-${version.value}"
assemblyJarName in assembly := s"${name.value}"
