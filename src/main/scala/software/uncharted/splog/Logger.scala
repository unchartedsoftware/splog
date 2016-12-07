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
import java.net.{InetAddress, Socket, ServerSocket}
import java.io.{StringWriter, PrintWriter, PrintStream}
import scala.io.{BufferedSource}
import scala.util.Try
import org.json.JSONObject

class Logger(port: Int, source: String) extends Serializable {
  import Level.{Level, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF} // scalastyle:ignore

  val driverHost: String = SparkContext.getOrCreate().getConf.get("spark.driver.host")

  def log(level: Level, msg: String, err: Option[Exception] = None): Unit = {
    Try({
      val s = new Socket(InetAddress.getByName(driverHost), port)
      val out = new PrintStream(s.getOutputStream())
      val payload = new JSONObject
      payload.put("level", level)
      payload.put("msg", msg)
      payload.put("source", source)
      if (err.isDefined) {
        val sw: StringWriter = new StringWriter()
        val pw: PrintWriter = new PrintWriter(sw)
        err.get.printStackTrace(pw)
        payload.put("errStack", sw.toString)
      }
      // not printing to stdout, so this is safe
      out.println(payload.toString) // scalastyle:ignore
      out.flush
      s.close
    })
  }

  def trace(msg: String): Unit = {
    this.log(TRACE, msg, None)
  }

  def trace(msg: String, err: Exception): Unit = {
    this.log(TRACE, msg, Some(err))
  }

  def debug(msg: String): Unit = {
    this.log(DEBUG, msg, None)
  }

  def debug(msg: String, err: Exception): Unit = {
    this.log(DEBUG, msg, Some(err))
  }

  def info(msg: String): Unit = {
    this.log(INFO, msg, None)
  }

  def info(msg: String, err: Exception): Unit = {
    this.log(INFO, msg, Some(err))
  }

  def warn(msg: String): Unit = {
    this.log(WARN, msg, None)
  }

  def warn(msg: String, err: Exception): Unit = {
    this.log(WARN, msg, Some(err))
  }

  def error(msg: String): Unit = {
    this.log(ERROR, msg, None)
  }

  def error(msg: String, err: Exception): Unit = {
    this.log(ERROR, msg, Some(err))
  }

  def fatal(msg: String): Unit = {
    this.log(FATAL, msg, None)
  }

  def fatal(msg: String, err: Exception): Unit = {
    this.log(FATAL, msg, Some(err))
  }

  // TODO implement specific logging functions, including error one which takes an Exception
}
