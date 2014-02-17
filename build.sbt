val scctInitialize = settingKey[Unit]("scctInitialize")

def setProp(key: String, value: String): Unit = {
  scala.Console.err.println(s"System property $key=$value")
  System.setProperty(key, value)
}

resolvers += Opts.resolver.sonatypeSnapshots

autoCompilerPlugins := true

scctInitialize := {
  setProp("scct.basedir", baseDirectory.value.getAbsolutePath)
  setProp("scct.report.hook", "system.property")
  setProp("scct.project.name", name.value)
  setProp("scct.source.dir", sourceDirectory.value.getAbsolutePath)
  setProp("scct-test-scala-version", scalaVersion.value)
  setProp("scct.report.dir", (baseDirectory.value / "scct").getAbsolutePath)
}

// There's some kind of conflict between exportJars and scct.
// exportJars in Global := true

initialCommands in console := s"cat ${baseDirectory.value}/src/main/resources/replStartup.scala".!!

lazy val utest = RootProject(uri("git://github.com/paulp/utest.git#paulp-2.11")) % "test"

// Coveralls token is set via environment variable COVERALLS_REPO_TOKEN
// In the case of travis this is encrypted into the .travis.yml file.
lazy val root = project in file(".") dependsOn (utest) settings (ScctPlugin.instrumentSettings ++ CoverallsPlugin.coverallsSettings: _*) settings (
  name                      := "psp-view",
  scalaVersion              := "2.11.0-M8",
  crossScalaVersions        := Seq(scalaVersion.value),
  organization              := "org.improving",
  version                   := "0.1.1-M1",
  description               := "psp alternate view implementation",
  homepage                  := Some(url("https://github.com/paulp/psp-view")),
  licenses                  := Seq("Apache" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  shellPrompt               := (s => name.value + projectString(s) + "> "),
  parallelExecution in Test := false,
  fork in Test              := true,
  libraryDependencies ++= Seq(
    "org.scala-lang"  % "scala-compiler"  % scalaVersion.value,
    "jline"           % "jline"           %       "2.11",
    "ch.qos.logback"  % "logback-classic" %      "1.0.9",
    "org.scalacheck" %% "scalacheck"      %      "1.11.3"       % "test",
    "com.sqality.scct" %% "scct" % "0.3.1-SNAPSHOT" % "scct"
  )
)

testFrameworks += new TestFramework("utest.runner.JvmFramework")

def projectString(s: State): String = (
  (Project extract s).currentRef.project.toString match {
    case s if s startsWith "default-" => ""
    case s                            => "#" + s
  }
)

scalacOptions ++= Seq(
  // "-Ylog:all"
  // "-Ydebug"
  // "-optimise"
  // "-Xlog-implicit-conversions"
)

publishTo := Some(Opts.resolver.mavenLocalFile)
