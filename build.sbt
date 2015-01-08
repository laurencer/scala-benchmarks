uniform.project("scala-benchmarks", "com.rouesnel.scala.benchmarks")    

uniformDependencySettings

uniformAssemblySettings

uniformThriftSettings

formatting.formatSettings

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

fork in Test := true

parallelExecution in Test := false

incOptions := incOptions.value.withNameHashing(true)

javaOptions ++= Seq("-Xms2048M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")

resolvers += "Sonatype OSS Snapshots" at
  "https://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= depend.scalaz() ++ depend.shapeless() ++ List(
  "com.storm-enroute" %% "scalameter" % "0.6"
)
