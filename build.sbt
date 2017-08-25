import com.typesafe.sbt.web.Import.WebKeys.webJarsDirectory

name := "Galapagos"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:_",
  "-Xlint",
  "-Ywarn-value-discard",
  "-Xfatal-warnings"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

val tortoiseVersion = "1.0-09a36ff-dirty"

libraryDependencies ++= Seq(
  ehcache,
  filters,
  guice,
  "org.nlogo" % "tortoise" % tortoiseVersion,
  "org.nlogo" % "netlogowebjs" % tortoiseVersion,
  "com.typesafe.play" %% "play-iteratees" % "2.6.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.1" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-RC1" % "test"
)

libraryDependencies ++= Seq(
  "org.webjars" % "chosen" % "1.3.0",
  "org.webjars.npm" % "filesaver.js" % "0.1.1",
  "org.webjars.npm" % "mousetrap" % "1.5.3",
  "org.webjars.bower" % "google-caja" % "6005.0.0",
  "org.webjars" % "highcharts" % "5.0.6",
  "org.webjars" % "jquery" % "3.1.1",
  "org.webjars" % "markdown-js" % "0.5.0-1",
  "org.webjars" % "ractive" % "0.7.3",
  "org.webjars" % "codemirror" % "5.13.2",
  "org.webjars.bower" % "github-com-highcharts-export-csv" % "1.4.3"
)

resolvers += bintray.Opts.resolver.repo("netlogo", "TortoiseAux")

resolvers += bintray.Opts.resolver.repo("netlogo", "NetLogoHeadless")

resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/" // Needed for akka-http (for now, at least) --JAB 5/23/17

GalapagosAssets.settings

// Used in Prod
pipelineStages ++= Seq(digest)

// Also used in Dev mode
pipelineStages in Assets ++= Seq(autoprefixer)

fork in Test := false

includeFilter in autoprefixer := Def.setting {
  val webJarDir     = (webJarsDirectory in Assets).value.getPath
  val testWebJarDir = (webJarsDirectory in TestAssets).value.getPath
  new FileFilter {
    override def accept(file: java.io.File) = {
      file.getName.endsWith(".css") && ! (file.getPath.contains(webJarDir) || file.getPath.contains(testWebJarDir))
    }
  }
}.value

routesGenerator := InjectedRoutesGenerator

def isTravis: Boolean = System.getenv("TRAVIS") == "true"

def travisBranch: String =
  if (System.getenv("TRAVIS_PULL_REQUEST") != "false")
    "PR-" + System.getenv("TRAVIS_PULL_REQUEST")
  else
    System.getenv("TRAVIS_BRANCH")
