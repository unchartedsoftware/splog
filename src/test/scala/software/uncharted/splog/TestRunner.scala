package software.uncharted.splog

import org.scalatest.tools.Runner

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Spark {
  val sparkSession = SparkSession.builder.appName("splog-test-runner").getOrCreate()
  val sc = sparkSession.sparkContext
}


object TestRunner {
  def main(args: Array[String]): Unit = {
    if (sys.env.isDefinedAt("SPARK_JAVA_OPTS") && sys.env("SPARK_JAVA_OPTS").contains("jdwp=transport=dt_socket")) {
      print("Sleeping for 8 seconds. Please connect your debugger now...")
      Thread.sleep(8000)
    }

    val testResult = Runner.run(Array("-o", "-R", "build/classes/test"))
    if (!testResult) {
      System.exit(1)
    }
  }
}
