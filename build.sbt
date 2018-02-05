name := "followermaze"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= {
  Seq(
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    "org.mockito" % "mockito-core" % "2.13.0" % Test
  )
}

fork in Test := false
parallelExecution in Test := false