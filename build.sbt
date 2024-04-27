resolvers += "Maven Central Server" at "https://repo1.maven.org/maven2"

name := "pyspark_listener"
version := "1.0.0"

val scalaVersion_ = "2.12.17"
val sparkVersion = "3.3.2"

libraryDependencies ++= List(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "net.sf.py4j" % "py4j" % "0.10.4"
)