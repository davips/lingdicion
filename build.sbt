name := "lingdicion"

version := "0.1"

//scalaVersion := "2.13.0"
scalaVersion := "2.11.12"

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

enablePlugins(ScalaNativePlugin)

libraryDependencies += "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.2"

//libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

