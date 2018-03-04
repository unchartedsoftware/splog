/*
 * Copyright 2016 Uncharted Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.uncharted.splog

import org.apache.spark.{TaskContext, SparkContext}
import com.typesafe.config.{Config, ConfigFactory}

object LoggerFactory {
  import Level.{Level, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF} // scalastyle:ignore

  private val conf: Config = ConfigFactory.load();
  private[splog] val port = conf.getInt("splog.port")
  private var level = Level.withName(conf.getString("splog.level"))
  private val threads = conf.getInt("splog.threads")
  private var dateFormat = conf.getString("splog.date.format")
  @transient private var receiver: Option[Receiver] = None;

  def setLevel(level: Level): Unit = {
    this.synchronized {
      LoggerFactory.level = level
    }
  }

  def getLevel: Level = {
    level
  }

  def start(out: java.io.PrintStream = Console.out): Unit = {
    this.synchronized {
      if (inDriver && !LoggerFactory.receiver.isDefined) {
        LoggerFactory.receiver = Some(new Receiver(LoggerFactory.port, dateFormat, out, threads))
        new Thread(LoggerFactory.receiver.get).start
      }
    }
  }

  def shutdown(): Unit = {
    this.synchronized {
      if (LoggerFactory.receiver.isDefined) {
        LoggerFactory.receiver.get.stop()
        LoggerFactory.receiver = None
      }
    }
  }

  def inDriver: Boolean = {
    TaskContext.get == null
  }

  def getLogger(source: String = "root", driverHost: Option[String] = None): Logger = {
    if (!inDriver) {
      throw new Exception("Cannot use getLogger() inside a Spark task (such as inside a map() closure)."
        + " Please instantiate your logger outside the closure and let Spark serialize it in.")
    } else {
      this.start()
      new Logger(port, source, driverHost.getOrElse(SparkContext.getOrCreate().getConf.get("spark.driver.host")))
    }
  }
}
