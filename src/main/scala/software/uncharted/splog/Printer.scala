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

import org.json.JSONObject
import java.io.PrintStream

class Printer(in: String, out: PrintStream, format: java.text.SimpleDateFormat) extends Runnable {

  import Level.{Level, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF} // scalastyle:ignore

  def doPrint(s: String): Unit = {
    out.println(s) // scalastyle:ignore
  }

  def logJsonMessage(raw: String): Unit = {
    val payload = new JSONObject(raw)
    val level = Level.withName(payload.getString("level"))
    val msg = payload.getString("msg")
    val source = payload.getString("source")
    val stack = if (payload.has("errStack")) Some(payload.getString("errStack")) else None
    this.logMessage(level, msg, source, stack)
  }

  def logMessage(
    level: Level,
    msg: String,
    source: String = "root",
    stack: Option[String] = None
  ) {
    val timestamp = format.format(new java.util.Date())
    // this can only run on the driver, so printing is safe
    if (level >= LoggerFactory.getLevel) {
      if (stack.isDefined) {
        this.doPrint(s"$timestamp [$level] $source: $msg\n${stack.get}") // scalastyle:ignore
      } else {
        this.doPrint(s"$timestamp [$level] $source: $msg") // scalastyle:ignore
      }
    }
  }

  def run() {
    this.logJsonMessage(in)
  }
}
